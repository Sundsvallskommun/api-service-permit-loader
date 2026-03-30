package se.sundsvall.permitloader.integration.party;

import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.permitloader.integration.party.configuration.PartyConfiguration;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@FeignClient(
	name = PartyConfiguration.REGISTRATION_ID,
	url = "${integration.party.base-url}",
	configuration = PartyConfiguration.class)
public interface PartyClient {

	@GetMapping(path = "/{municipalityId}/PRIVATE/{legalId}/partyId", produces = TEXT_PLAIN_VALUE)
	Optional<String> getPartyId(
		@PathVariable final String municipalityId,
		@PathVariable final String legalId);
}
