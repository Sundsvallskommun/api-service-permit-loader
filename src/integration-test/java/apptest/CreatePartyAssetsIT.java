package apptest;

import java.util.stream.LongStream;
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
	files = "classpath:/CreatePartyAssetsIT/",
	classes = Application.class)
@Sql(scripts = {
	"/db/truncate.sql",
	"/db/testdata-it-create.sql"
})
class CreatePartyAssetsIT extends AbstractAppTest {

	private static final String PATH = "/2281/permits/create-party-assets";

	@Autowired
	private ProcapitaRawRepository repository;

	@Test
	void test1_successfulWithAllAssistanceTypes() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequestAndVerifyResponse();

		// FARDTJANST rows (ids 1-16, 29) should have the FT asset id
		final var ftIds = java.util.stream.Stream.concat(
			LongStream.rangeClosed(1, 16).boxed(),
			java.util.stream.Stream.of(29L)).toList();
		final var ftRecords = repository.findAllById(ftIds);
		assertThat(ftRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isEqualTo("ft-asset-1111-2222-3333-444444444444");
			assertThat(entity.getStatus()).isEqualTo("ASSET_CREATED");
		});

		// RIKSFARDTJANST rows (ids 17-28) should have the RFT asset id
		final var rftIds = LongStream.rangeClosed(17, 28).boxed().toList();
		final var rftRecords = repository.findAllById(rftIds);
		assertThat(rftRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isEqualTo("rft-asset-5555-6666-7777-888888888888");
			assertThat(entity.getStatus()).isEqualTo("ASSET_CREATED");
		});
	}

	@Test
	void test2_partyAssetsError() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequestAndVerifyResponse();

		// All rows should have error status and no asset id
		final var allIds = java.util.stream.Stream.concat(
			LongStream.rangeClosed(1, 28).boxed(),
			java.util.stream.Stream.of(29L)).toList();
		final var allRecords = repository.findAllById(allIds);
		assertThat(allRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isNull();
			assertThat(entity.getStatus()).startsWith("ASSET_CREATION_ERROR:");
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

		// FARDTJANST rows (ids 1-16, 29) should succeed
		final var ftIds = java.util.stream.Stream.concat(
			LongStream.rangeClosed(1, 16).boxed(),
			java.util.stream.Stream.of(29L)).toList();
		final var ftRecords = repository.findAllById(ftIds);
		assertThat(ftRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isEqualTo("ft-asset-1111-2222-3333-444444444444");
			assertThat(entity.getStatus()).isEqualTo("ASSET_CREATED");
		});

		// RIKSFARDTJANST rows (ids 17-28) should have error status
		final var rftIds = LongStream.rangeClosed(17, 28).boxed().toList();
		final var rftRecords = repository.findAllById(rftIds);
		assertThat(rftRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isNull();
			assertThat(entity.getStatus()).startsWith("ASSET_CREATION_ERROR:");
		});
	}
}
