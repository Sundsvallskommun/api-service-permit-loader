package se.sundsvall.permitloader.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class ProcapitaRawRepositoryTest {

	@Autowired
	private ProcapitaRawRepository repository;

	@Test
	void testFindByPartyIdIsNull() {
		// Person 1 has 7 rows without partyId (ids 1-7)
		final var result = repository.findByPersonalNumberIsNotNullAndPartyIdIsNull();

		assertThat(result)
			.hasSize(7)
			.allSatisfy(entity -> assertThat(entity.getPartyId()).isNull())
			.extracting("personalNumber").containsOnly("199001011234");
	}

	@Test
	void testFindByPartyIdIsNotNullAndPartyAssetIdIsNull() {
		// Person 2 has 4 rows with partyId but no partyAssetId (ids 8-11)
		final var result = repository.findByPartyIdIsNotNullAndPartyAssetIdIsNull();

		assertThat(result)
			.hasSize(4)
			.allSatisfy(entity -> {
				assertThat(entity.getPartyId()).isNotNull();
				assertThat(entity.getPartyAssetId()).isNull();
			})
			.extracting("personalNumber").containsOnly("198505055678");
	}

	@Test
	void testFullyProcessedRecordsAreExcluded() {
		// Person 3 (id 12) is fully processed and should not appear in either query
		final var withoutPartyId = repository.findByPersonalNumberIsNotNullAndPartyIdIsNull();
		final var withoutAssetId = repository.findByPartyIdIsNotNullAndPartyAssetIdIsNull();

		assertThat(withoutPartyId).isNotEmpty().extracting("id").doesNotContain(12L);
		assertThat(withoutAssetId).isNotEmpty().extracting("id").doesNotContain(12L);
	}

	@Test
	void testNullPersonalNumberIsExcluded() {
		// Row 13 has NULL personal_number and should not be returned by findByPersonalNumberIsNotNullAndPartyIdIsNull
		final var result = repository.findByPersonalNumberIsNotNullAndPartyIdIsNull();

		assertThat(result).isNotEmpty().extracting("id").doesNotContain(13L);
	}

	@Test
	void testFindAll() {
		final var result = repository.findAll();

		assertThat(result).hasSize(13);
	}

	@Test
	void testEntityFieldMapping() {
		final var entity = repository.findById(8L).orElseThrow();

		assertThat(entity).satisfies(e -> {
			assertThat(e.getPersonalNumber()).isEqualTo("198505055678");
			assertThat(e.getAssistanceType()).isEqualTo("Privatresor");
			assertThat(e.getDuration()).isEqualTo("6 mån");
			assertThat(e.getStartDate()).isEqualTo("2026-02-01");
			assertThat(e.getEndDate()).isEqualTo("2026-08-01");
			assertThat(e.getPermitGroup()).isEqualTo("FARDTJANST");
			assertThat(e.getPartyId()).isEqualTo("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
			assertThat(e.getPartyAssetId()).isNull();
			assertThat(e.getStatus()).isEqualTo("PARTY_ID_FETCHED");
		});
	}

	@Test
	void testSaveAndUpdate() {
		final var entity = repository.findById(1L).orElseThrow();

		assertThat(entity.getPartyId()).isNull();

		entity.setPartyId("new-party-id");
		entity.setStatus("PARTY_ID_FETCHED");
		repository.save(entity);

		final var updated = repository.findById(1L).orElseThrow();
		assertThat(updated).satisfies(e -> {
			assertThat(e.getPartyId()).isEqualTo("new-party-id");
			assertThat(e.getStatus()).isEqualTo("PARTY_ID_FETCHED");
		});

		// Should now have one less in the "without partyId" query
		final var withoutPartyId = repository.findByPersonalNumberIsNotNullAndPartyIdIsNull();
		assertThat(withoutPartyId).hasSize(6);
	}
}
