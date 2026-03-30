package se.sundsvall.permitloader.service.mapper;

import generated.se.sundsvall.partyassets.AssetCreateRequest;
import generated.se.sundsvall.partyassets.AssetJsonParameter;
import generated.se.sundsvall.partyassets.Status;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;

public final class PermitMapper {

	private static final String SCHEMA_ID = "2281_paratransit_2.0";
	private static final String ORIGIN = "Procapita";

	private static final Map<String, String> PERMIT_GROUP_TO_TYPE = Map.of(
		"FARDTJANST", "ParatransitPermitLocal",
		"RIKSFARDTJANST", "ParatransitPermitNational");

	private static final Map<String, String> PERMIT_GROUP_TO_DESCRIPTION = Map.of(
		"FARDTJANST", "Färdtjänst",
		"RIKSFARDTJANST", "Riksfärdtjänst");

	private PermitMapper() {}

	public static AssetCreateRequest toAssetCreateRequest(final String permitGroup, final List<ProcapitaRawEntity> rows) {
		final var type = PERMIT_GROUP_TO_TYPE.getOrDefault(permitGroup, permitGroup);

		final var request = new AssetCreateRequest();
		request.setPartyId(rows.getFirst().getPartyId());
		request.setType(type);
		request.setDescription(PERMIT_GROUP_TO_DESCRIPTION.getOrDefault(permitGroup, permitGroup));
		request.setOrigin(ORIGIN);
		request.setStatus(Status.ACTIVE);

		rows.stream()
			.map(ProcapitaRawEntity::getStartDate)
			.filter(Objects::nonNull)
			.min(LocalDate::compareTo)
			.ifPresent(request::setIssued);

		final var allHaveEndDate = rows.stream().map(ProcapitaRawEntity::getEndDate).allMatch(Objects::nonNull);
		if (allHaveEndDate) {
			rows.stream()
				.map(ProcapitaRawEntity::getEndDate)
				.max(LocalDate::compareTo)
				.ifPresent(request::setValidTo);
		}

		request.setAdditionalParameters(Map.of(
			"migratedFrom", ORIGIN,
			"migratedAt", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

		final var builder = new JsonValueBuilder();
		rows.forEach(row -> AssistanceTypeMapper.applyAssistanceType(row.getAssistanceType(), builder));

		final var jsonParam = new AssetJsonParameter();
		jsonParam.setSchemaId(SCHEMA_ID);
		jsonParam.setKey(type);
		jsonParam.setValue(builder.build());
		request.setJsonParameters(List.of(jsonParam));

		return request;
	}
}
