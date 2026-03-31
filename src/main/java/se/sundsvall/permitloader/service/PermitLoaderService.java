package se.sundsvall.permitloader.service;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.permitloader.api.model.JobSummary;
import se.sundsvall.permitloader.integration.db.ProcapitaRawRepository;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;
import se.sundsvall.permitloader.integration.party.PartyClient;
import se.sundsvall.permitloader.integration.partyassets.PartyAssetsClient;

import static java.lang.Math.max;
import static java.util.stream.Collectors.groupingBy;
import static se.sundsvall.permitloader.service.mapper.PermitMapper.toAssetCreateRequest;

@Service
public class PermitLoaderService {

	private static final Logger LOG = LoggerFactory.getLogger(PermitLoaderService.class);

	private final ProcapitaRawRepository repository;
	private final PartyClient partyClient;
	private final PartyAssetsClient partyAssetsClient;
	private final TransactionalHelper transactionalHelper;

	public PermitLoaderService(final ProcapitaRawRepository repository,
		final PartyClient partyClient,
		final PartyAssetsClient partyAssetsClient,
		final TransactionalHelper transactionalHelper) {

		this.repository = repository;
		this.partyClient = partyClient;
		this.partyAssetsClient = partyAssetsClient;
		this.transactionalHelper = transactionalHelper;
	}

	public JobSummary fetchPartyIds(final String municipalityId) {
		final var records = repository.findByPersonalNumberIsNotNullAndPartyIdIsNull();
		LOG.info("Found {} records without partyId", records.size());

		final var byPersonalNumber = records.stream().collect(groupingBy(ProcapitaRawEntity::getPersonalNumber));

		int successCount = 0;
		int errorCount = 0;

		for (final var entry : byPersonalNumber.entrySet()) {
			final var personalNumber = entry.getKey();
			final var rows = entry.getValue();

			try {
				final var partyId = partyClient.getPartyId(municipalityId, personalNumber);
				if (partyId.isEmpty()) {
					LOG.atWarn().setMessage("No partyId found for personalNumber ending in '...{}'").addArgument(() -> mask(personalNumber)).log();
					errorCount++;
					continue;
				}
				updateRows(rows, row -> {
					row.setPartyId(partyId.get());
					row.setStatus("PARTY_ID_FETCHED");
				});
				successCount++;
				LOG.atInfo().setMessage("Fetched partyId for personalNumber ending in '...{}'").addArgument(() -> mask(personalNumber)).log();
			} catch (final Exception e) {
				errorCount++;
				LOG.atError().setMessage("Failed to fetch partyId for personalNumber ending in '...{}': {}").addArgument(() -> mask(personalNumber)).addArgument(e::getMessage).log();
				updateRows(rows, row -> row.setStatus("PARTY_ID_ERROR: " + e.getMessage()));
			}
		}

		LOG.info("fetchPartyIds completed. Total: {}, Success: {}, Errors: {}", byPersonalNumber.size(), successCount, errorCount);
		return new JobSummary(byPersonalNumber.size(), successCount, errorCount);
	}

	public JobSummary createPartyAssets(final String municipalityId, final Integer limit) {
		final var records = repository.findByPartyIdIsNotNullAndPartyAssetIdIsNull();
		LOG.info("Found {} records to create party assets for", records.size());

		final var groups = records.stream().collect(groupingBy(row -> row.getPersonalNumber() + "|" + row.getPermitGroup()));

		int successCount = 0;
		int errorCount = 0;
		int processed = 0;
		for (final var entry : groups.entrySet()) {

			if (limit != null && processed >= limit) {
				break;
			}

			final var rows = entry.getValue();
			final var permitGroup = rows.getFirst().getPermitGroup();

			try {
				final var request = toAssetCreateRequest(permitGroup, rows);
				final var response = partyAssetsClient.createAsset(municipalityId, request);
				final var location = response.getHeaders().getLocation();
				final var partyAssetId = location != null ? extractIdFromLocation(location) : null;

				updateRows(rows, row -> {
					row.setPartyAssetId(partyAssetId);
					row.setStatus("ASSET_CREATED");
				});
				successCount++;
				LOG.info("Created party asset {} for group {}", partyAssetId, entry.getKey());
			} catch (final Exception e) {
				errorCount++;
				LOG.error("Failed to create party asset for group {}: {}", entry.getKey(), e.getMessage());
				updateRows(rows, row -> row.setStatus("ASSET_CREATION_ERROR: " + e.getMessage()));
			}
			processed++;
		}

		LOG.info("createPartyAssets completed. Total groups: {}, Processed: {}, Success: {}, Errors: {}", groups.size(), processed, successCount, errorCount);
		return new JobSummary(processed, successCount, errorCount);
	}

	private void updateRows(final List<ProcapitaRawEntity> rows, final Consumer<ProcapitaRawEntity> updater) {
		for (final var row : rows) {
			try {
				updater.accept(row);
				transactionalHelper.saveEntity(row);
			} catch (final Exception e) {
				LOG.error("Failed to save record {}: {}", row.getId(), e.getMessage());
			}
		}
	}

	private String extractIdFromLocation(final URI location) {
		final var path = location.getPath();
		return path.substring(path.lastIndexOf('/') + 1);
	}

	private static String mask(final String personalNumber) {
		return personalNumber.substring(max(0, personalNumber.length() - 4));
	}
}
