package se.sundsvall.permitloader.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.permitloader.api.model.JobSummary;
import se.sundsvall.permitloader.service.PermitLoaderService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/{municipalityId}/permits")
class PermitLoaderResource {

	private final PermitLoaderService service;

	public PermitLoaderResource(final PermitLoaderService service) {
		this.service = service;
	}

	@PostMapping(value = "/fetch-party-ids", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<JobSummary> fetchPartyIds(@PathVariable final String municipalityId) {
		return ResponseEntity.ok(service.fetchPartyIds(municipalityId));
	}

	@PostMapping(value = "/create-party-assets", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<JobSummary> createPartyAssets(@PathVariable final String municipalityId) {
		return ResponseEntity.ok(service.createPartyAssets(municipalityId));
	}
}
