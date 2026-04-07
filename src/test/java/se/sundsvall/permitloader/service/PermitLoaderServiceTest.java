package se.sundsvall.permitloader.service;

import generated.se.sundsvall.partyassets.AssetCreateRequest;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import se.sundsvall.permitloader.integration.db.ProcapitaRawRepository;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;
import se.sundsvall.permitloader.integration.party.PartyClient;
import se.sundsvall.permitloader.integration.partyassets.PartyAssetsClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitLoaderServiceTest {

	@Mock
	private ProcapitaRawRepository repository;

	@Mock
	private PartyClient partyClient;

	@Mock
	private PartyAssetsClient partyAssetsClient;

	@Mock
	private TransactionalHelper transactionalHelper;

	@InjectMocks
	private PermitLoaderService service;

	@Test
	void testFetchPartyIdsSuccess() {
		final var entity1 = createEntity(1L, "199001011234", "FARDTJANST", "Buss");
		final var entity2 = createEntity(2L, "199001011234", "FARDTJANST", "Rollator");

		when(repository.findByPersonalNumberIsNotNullAndPartyIdIsNull()).thenReturn(List.of(entity1, entity2));
		when(partyClient.getPartyId("2281", "199001011234")).thenReturn(java.util.Optional.of("party-uuid-123"));
		doNothing().when(transactionalHelper).saveEntity(any());

		final var result = service.fetchPartyIds("2281");

		assertThat(result.totalProcessed()).isEqualTo(1);
		assertThat(result.successCount()).isEqualTo(1);
		assertThat(result.errorCount()).isZero();
		assertThat(entity1.getPartyId()).isEqualTo("party-uuid-123");
		assertThat(entity2.getPartyId()).isEqualTo("party-uuid-123");
		verify(transactionalHelper, times(2)).saveEntity(any());
	}

	@Test
	void testFetchPartyIdsWithError() {
		final var entity = createEntity(1L, "199001011234", "FARDTJANST", "Buss");

		when(repository.findByPersonalNumberIsNotNullAndPartyIdIsNull()).thenReturn(List.of(entity));
		when(partyClient.getPartyId("2281", "199001011234")).thenThrow(new RuntimeException("Not found"));
		doNothing().when(transactionalHelper).saveEntity(any());

		final var result = service.fetchPartyIds("2281");

		assertThat(result.totalProcessed()).isEqualTo(1);
		assertThat(result.successCount()).isZero();
		assertThat(result.errorCount()).isEqualTo(1);
		assertThat(entity.getStatus()).startsWith("PARTY_ID_ERROR:");
	}

	@Test
	void testCreatePartyAssetsSuccess() {
		final var entity1 = createEntityWithPartyId(1L, "199001011234", "FARDTJANST", "Buss", "party-uuid-123");
		final var entity2 = createEntityWithPartyId(2L, "199001011234", "FARDTJANST", "Rollator", "party-uuid-123");

		when(repository.findByPartyIdIsNotNullAndPartyAssetIdIsNull()).thenReturn(List.of(entity1, entity2));

		final ResponseEntity<Void> response = ResponseEntity.created(URI.create("/2281/assets/asset-uuid-456")).build();
		when(partyAssetsClient.createAsset(eq("2281"), any(AssetCreateRequest.class))).thenReturn(response);
		doNothing().when(transactionalHelper).saveEntity(any());

		final var result = service.createPartyAssets("2281", null);

		assertThat(result.totalProcessed()).isEqualTo(1);
		assertThat(result.successCount()).isEqualTo(1);
		assertThat(result.errorCount()).isZero();
		assertThat(entity1.getPartyAssetId()).isEqualTo("asset-uuid-456");
		assertThat(entity2.getPartyAssetId()).isEqualTo("asset-uuid-456");
		assertThat(entity1.getStatus()).isEqualTo("ASSET_CREATED");
		verify(transactionalHelper, times(2)).saveEntity(any());
	}

	@Test
	void testCreatePartyAssetsWithError() {
		final var entity = createEntityWithPartyId(1L, "199001011234", "FARDTJANST", "Buss", "party-uuid-123");

		when(repository.findByPartyIdIsNotNullAndPartyAssetIdIsNull()).thenReturn(List.of(entity));
		when(partyAssetsClient.createAsset(eq("2281"), any(AssetCreateRequest.class)))
			.thenThrow(new RuntimeException("Service unavailable"));
		doNothing().when(transactionalHelper).saveEntity(any());

		final var result = service.createPartyAssets("2281", null);

		assertThat(result.totalProcessed()).isEqualTo(1);
		assertThat(result.successCount()).isZero();
		assertThat(result.errorCount()).isEqualTo(1);
		assertThat(entity.getStatus()).startsWith("ASSET_CREATION_ERROR:");
	}

	@Test
	void testCreatePartyAssetsGroupsByPersonalNumberAndPermitGroup() {
		final var ftEntity = createEntityWithPartyId(1L, "199001011234", "FARDTJANST", "Buss", "party-uuid-123");
		final var rftEntity = createEntityWithPartyId(2L, "199001011234", "RIKSFARDTJANST", "Flyg", "party-uuid-123");

		when(repository.findByPartyIdIsNotNullAndPartyAssetIdIsNull()).thenReturn(List.of(ftEntity, rftEntity));

		final ResponseEntity<Void> response = ResponseEntity.created(URI.create("/2281/assets/uuid-1")).build();
		when(partyAssetsClient.createAsset(eq("2281"), any(AssetCreateRequest.class))).thenReturn(response);
		doNothing().when(transactionalHelper).saveEntity(any());

		final var result = service.createPartyAssets("2281", null);

		// Two groups: one for FARDTJANST, one for RIKSFARDTJANST
		assertThat(result.totalProcessed()).isEqualTo(2);
		assertThat(result.successCount()).isEqualTo(2);
		verify(partyAssetsClient, times(2)).createAsset(eq("2281"), any(AssetCreateRequest.class));
	}

	private ProcapitaRawEntity createEntity(final Long id, final String personalNumber, final String permitGroup, final String assistanceType) {
		final var entity = new ProcapitaRawEntity();
		entity.setId(id);
		entity.setPersonalNumber(personalNumber);
		entity.setPermitGroup(permitGroup);
		entity.setAssistanceType(assistanceType);
		entity.setStartDate(LocalDate.of(2026, 1, 1));
		entity.setEndDate(LocalDate.of(2027, 1, 1));
		return entity;
	}

	private ProcapitaRawEntity createEntityWithPartyId(final Long id, final String personalNumber, final String permitGroup,
		final String assistanceType, final String partyId) {
		final var entity = createEntity(id, personalNumber, permitGroup, assistanceType);
		entity.setPartyId(partyId);
		return entity;
	}
}
