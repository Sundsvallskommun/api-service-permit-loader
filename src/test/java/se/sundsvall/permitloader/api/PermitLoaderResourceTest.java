package se.sundsvall.permitloader.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.permitloader.api.model.JobSummary;
import se.sundsvall.permitloader.service.PermitLoaderService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class PermitLoaderResourceTest {

	@Mock
	private PermitLoaderService service;

	@InjectMocks
	private PermitLoaderResource resource;

	@Test
	void testFetchPartyIds() {
		when(service.fetchPartyIds("2281")).thenReturn(new JobSummary(10, 8, 2));

		final var response = resource.fetchPartyIds("2281");

		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().totalProcessed()).isEqualTo(10);
		assertThat(response.getBody().successCount()).isEqualTo(8);
		assertThat(response.getBody().errorCount()).isEqualTo(2);
		verify(service).fetchPartyIds("2281");
	}

	@Test
	void testCreatePartyAssets() {
		when(service.createPartyAssets("2281", null)).thenReturn(new JobSummary(5, 5, 0));

		final var response = resource.createPartyAssets("2281", null);

		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().totalProcessed()).isEqualTo(5);
		assertThat(response.getBody().successCount()).isEqualTo(5);
		assertThat(response.getBody().errorCount()).isZero();
		verify(service).createPartyAssets("2281", null);
	}
}
