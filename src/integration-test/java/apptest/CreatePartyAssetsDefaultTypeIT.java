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
	files = "classpath:/CreatePartyAssetsDefaultTypeIT/",
	classes = Application.class)
@Sql(scripts = {
	"/db/truncate.sql",
	"/db/testdata-it-create-default-type.sql"
})
class CreatePartyAssetsDefaultTypeIT extends AbstractAppTest {

	private static final String PATH = "/2281/permits/create-party-assets";

	@Autowired
	private ProcapitaRawRepository repository;

	@Test
	void test1_defaultTypeForFardtjanst() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequestAndVerifyResponse();

		// FARDTJANST rows (ids 1-2) should have default type applied and status_details set
		final var ftRecords = repository.findAllById(List.of(1L, 2L));
		assertThat(ftRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isEqualTo("ft-default-1111-2222-3333-444444444444");
			assertThat(entity.getStatus()).isEqualTo("ASSET_CREATED");
			assertThat(entity.getStatusDetails()).isEqualTo("jsonParameters.type defaulted to 'privat_fritid' (no matching type mapped from assistance_type)");
		});
	}

	@Test
	void test2_defaultTypeForRiksfardtjanst() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("expected-response.json")
			.sendRequest();

		// RIKSFARDTJANST rows (ids 3-4) should have default type applied and status_details set
		final var rftRecords = repository.findAllById(List.of(3L, 4L));
		assertThat(rftRecords).isNotEmpty().allSatisfy(entity -> {
			assertThat(entity.getPartyAssetId()).isEqualTo("rft-default-5555-6666-7777-888888888888");
			assertThat(entity.getStatus()).isEqualTo("ASSET_CREATED");
			assertThat(entity.getStatusDetails()).isEqualTo("jsonParameters.type defaulted to 'generellt_tillstand' (no matching type mapped from assistance_type)");
		});
	}
}
