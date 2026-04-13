package se.sundsvall.permitloader.service.mapper;

import generated.se.sundsvall.partyassets.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PermitMapperTest {

	@Test
	void testToAssetCreateRequestForFardtjanst() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Arbetsresor",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");
		final var row3 = createEntity(3L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), null, "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2, row3));
		final var request = result.request();

		assertThat(request.getType()).isEqualTo("ParatransitPermitLocal");
		assertThat(request.getDescription()).isEqualTo("Färdtjänst");
		assertThat(request.getPartyId()).isEqualTo("party-id-123");
		assertThat(request.getOrigin()).isEqualTo("Procapita");
		assertThat(request.getStatus()).isEqualTo(Status.ACTIVE);
		assertThat(request.getIssued()).isEqualTo(LocalDate.of(2026, 1, 1));
		assertThat(request.getValidTo()).isNull(); // row3 has null end_date
		assertThat(request.getAdditionalParameters())
			.containsEntry("migratedFrom", "Procapita")
			.containsKey("migratedAt");
		assertThat(request.getJsonParameters()).hasSize(1);

		final var jsonParam = request.getJsonParameters().getFirst();
		assertThat(jsonParam.getSchemaId()).isEqualTo("2281_paratransitpermitlocal_2.2");
		assertThat(jsonParam.getKey()).isEqualTo("ParatransitPermitLocal");
		assertThat(result.statusDetails()).isNull();
	}

	@Test
	void testToAssetCreateRequestForRiksfardtjanst() {
		final var row = createEntity(1L, "199001011234", "RIKSFARDTJANST", "Generellt tillstånd",
			LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), "party-id-456");

		final var result = PermitMapper.toAssetCreateRequest("RIKSFARDTJANST", List.of(row));
		final var request = result.request();

		assertThat(request.getType()).isEqualTo("ParatransitPermitNational");
		assertThat(request.getDescription()).isEqualTo("Riksfärdtjänst");
		assertThat(request.getIssued()).isEqualTo(LocalDate.of(2026, 3, 1));
		assertThat(request.getValidTo()).isEqualTo(LocalDate.of(2027, 3, 1));

		final var jsonParam = request.getJsonParameters().getFirst();
		assertThat(jsonParam.getSchemaId()).isEqualTo("2281_paratransitpermitnational_2.2");
		assertThat(jsonParam.getKey()).isEqualTo("ParatransitPermitNational");
		assertThat(result.statusDetails()).isNull();
	}

	@Test
	void testIssuedUsesEarliestStartDate() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 6, 1), LocalDate.of(2027, 6, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 6, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2));

		assertThat(result.request().getIssued()).isEqualTo(LocalDate.of(2026, 1, 1));
	}

	@Test
	void testValidToUsesLatestEndDate() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), LocalDate.of(2028, 1, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2));

		assertThat(result.request().getValidTo()).isEqualTo(LocalDate.of(2028, 1, 1));
	}

	@Test
	void testValidToIsNullWhenAnyRowLacksEndDate() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), null, "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2));

		assertThat(result.request().getValidTo()).isNull();
	}

	@Test
	void testDefaultTypeForFardtjanstWhenNoTypeMapped() {
		final var row = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row));

		@SuppressWarnings("unchecked")
		final var jsonValue = (Map<String, Object>) result.request().getJsonParameters().getFirst().getValue();
		assertThat(jsonValue).containsEntry("type", List.of("privat_fritid"));
		assertThat(result.statusDetails()).isEqualTo("jsonParameters.type defaulted to 'privat_fritid' (no matching type mapped from assistance_type)");
	}

	@Test
	void testDefaultTypeForRiksfardtjanstWhenNoTypeMapped() {
		final var row = createEntity(1L, "199001011234", "RIKSFARDTJANST", "Flyg",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-456");

		final var result = PermitMapper.toAssetCreateRequest("RIKSFARDTJANST", List.of(row));

		@SuppressWarnings("unchecked")
		final var jsonValue = (Map<String, Object>) result.request().getJsonParameters().getFirst().getValue();
		assertThat(jsonValue).containsEntry("type", List.of("generellt_tillstand"));
		assertThat(result.statusDetails()).isEqualTo("jsonParameters.type defaulted to 'generellt_tillstand' (no matching type mapped from assistance_type)");
	}

	@Test
	void testEmptyTransportModeWhenNoTransportModeMapped() {
		final var row = createEntity(1L, "199001011234", "FARDTJANST", "Arbetsresor",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row));

		@SuppressWarnings("unchecked")
		final var jsonValue = (Map<String, Object>) result.request().getJsonParameters().getFirst().getValue();
		assertThat(jsonValue).containsEntry("transportMode", List.of());
	}

	@Test
	void testNoDefaultTypeWhenTypeMapped() {
		final var row = createEntity(1L, "199001011234", "FARDTJANST", "Arbetsresor",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row));

		@SuppressWarnings("unchecked")
		final var jsonValue = (Map<String, Object>) result.request().getJsonParameters().getFirst().getValue();
		assertThat(jsonValue).containsEntry("type", List.of("arbetsresor"));
		assertThat(result.statusDetails()).isNull();
	}

	private ProcapitaRawEntity createEntity(final Long id, final String personalNumber, final String permitGroup,
		final String assistanceType, final LocalDate startDate, final LocalDate endDate, final String partyId) {
		final var entity = new ProcapitaRawEntity();
		entity.setId(id);
		entity.setPersonalNumber(personalNumber);
		entity.setPermitGroup(permitGroup);
		entity.setAssistanceType(assistanceType);
		entity.setStartDate(startDate);
		entity.setEndDate(endDate);
		entity.setPartyId(partyId);
		return entity;
	}
}
