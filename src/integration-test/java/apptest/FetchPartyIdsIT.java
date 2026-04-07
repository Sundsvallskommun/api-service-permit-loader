package apptest;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.permitloader.Application;
import se.sundsvall.permitloader.integration.db.ProcapitaRawRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(
	files = "classpath:/FetchPartyIdsIT/",
	classes = Application.class)
@Sql(scripts = {
	"/db/truncate.sql",
	"/db/testdata-it-fetch.sql"
})
class FetchPartyIdsIT extends AbstractAppTest {

	private static final String PATH = "/2281/permits/fetch-party-ids";

	@Autowired
	private ProcapitaRawRepository repository;

	@Test
	void test1_successfulForMultiplePersons() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequestAndVerifyResponse();

		// Person A: all 3 rows should have partyId set
		final var personA = repository.findAllById(List.of(1L, 2L, 3L));
		assertThat(personA).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyId()).isEqualTo("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
			assertThat(entity.getStatus()).isEqualTo("PARTY_ID_FETCHED");
		});

		// Person B: all 2 rows should have partyId set
		final var personB = repository.findAllById(List.of(4L, 5L));
		assertThat(personB).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyId()).isEqualTo("11111111-2222-3333-4444-555555555555");
			assertThat(entity.getStatus()).isEqualTo("PARTY_ID_FETCHED");
		});

		// Person C: should be unchanged (already had partyId)
		final var personC = repository.findById(6L).orElseThrow();
		assertThat(personC.getPartyId()).isEqualTo("already-has-party-id");
	}

	@Test
	void test2_partyNotFound() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequest();

		// Both persons should have error status, partyId still null
		final var allRecords = repository.findAllById(List.of(1L, 2L, 3L, 4L, 5L));
		assertThat(allRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyId()).isNull();
			assertThat(entity.getStatus()).startsWith("PARTY_ID_ERROR:");
		});
	}

	@Test
	void test3_mixedResults() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequest();

		// Person A: success
		final var personA = repository.findAllById(List.of(1L, 2L, 3L));
		assertThat(personA).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyId()).isEqualTo("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
			assertThat(entity.getStatus()).isEqualTo("PARTY_ID_FETCHED");
		});

		// Person B: error (404)
		final var personB = repository.findAllById(List.of(4L, 5L));
		assertThat(personB).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyId()).isNull();
			assertThat(entity.getStatus()).startsWith("PARTY_ID_ERROR:");
		});
	}
}
