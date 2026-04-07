package se.sundsvall.permitloader.integration.partyassets;

import generated.se.sundsvall.partyassets.AssetCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.permitloader.integration.partyassets.configuration.PartyAssetsConfiguration;

@FeignClient(
	name = PartyAssetsConfiguration.REGISTRATION_ID,
	url = "${integration.partyassets.base-url}",
	configuration = PartyAssetsConfiguration.class)
public interface PartyAssetsClient {

	@PostMapping("/{municipalityId}/assets")
	ResponseEntity<Void> createAsset(
		@PathVariable final String municipalityId,
		@RequestBody final AssetCreateRequest request);
}
