package se.sundsvall.permitloader.service.mapper;

import generated.se.sundsvall.partyassets.Status;
import java.time.LocalDate;
import java.util.List;
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

		assertThat(result.getType()).isEqualTo("ParatransitPermitLocal");
		assertThat(result.getDescription()).isEqualTo("Färdtjänst");
		assertThat(result.getPartyId()).isEqualTo("party-id-123");
		assertThat(result.getOrigin()).isEqualTo("Procapita");
		assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
		assertThat(result.getIssued()).isEqualTo(LocalDate.of(2026, 1, 1));
		assertThat(result.getValidTo()).isNull(); // row3 has null end_date
		assertThat(result.getAdditionalParameters())
			.containsEntry("migratedFrom", "Procapita")
			.containsKey("migratedAt");
		assertThat(result.getJsonParameters()).hasSize(1);

		final var jsonParam = result.getJsonParameters().getFirst();
		assertThat(jsonParam.getSchemaId()).isEqualTo("2281_paratransitpermitlocal_2.2");
		assertThat(jsonParam.getKey()).isEqualTo("ParatransitPermitLocal");
	}

	@Test
	void testToAssetCreateRequestForRiksfardtjanst() {
		final var row = createEntity(1L, "199001011234", "RIKSFARDTJANST", "Generellt tillstånd",
			LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), "party-id-456");

		final var result = PermitMapper.toAssetCreateRequest("RIKSFARDTJANST", List.of(row));

		assertThat(result.getType()).isEqualTo("ParatransitPermitNational");
		assertThat(result.getDescription()).isEqualTo("Riksfärdtjänst");
		assertThat(result.getIssued()).isEqualTo(LocalDate.of(2026, 3, 1));
		assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2027, 3, 1));

		final var jsonParam = result.getJsonParameters().getFirst();
		assertThat(jsonParam.getSchemaId()).isEqualTo("2281_paratransitpermitnational_2.2");
		assertThat(jsonParam.getKey()).isEqualTo("ParatransitPermitNational");
	}

	@Test
	void testIssuedUsesEarliestStartDate() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 6, 1), LocalDate.of(2027, 6, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 6, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2));

		assertThat(result.getIssued()).isEqualTo(LocalDate.of(2026, 1, 1));
	}

	@Test
	void testValidToUsesLatestEndDate() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), LocalDate.of(2028, 1, 1), "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2));

		assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2028, 1, 1));
	}

	@Test
	void testValidToIsNullWhenAnyRowLacksEndDate() {
		final var row1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss",
			LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), "party-id-123");
		final var row2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator",
			LocalDate.of(2026, 1, 1), null, "party-id-123");

		final var result = PermitMapper.toAssetCreateRequest("FARDTJANST", List.of(row1, row2));

		assertThat(result.getValidTo()).isNull();
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
