package se.sundsvall.permitloader.service.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonValueBuilder {

	private final Set<String> type = new LinkedHashSet<>();
	private final Set<String> transportMode = new LinkedHashSet<>();
	private final Set<String> additionalAids = new LinkedHashSet<>();
	private final Set<String> mobilityAids = new LinkedHashSet<>();
	private boolean winterService;
	private final List<String> notes = new ArrayList<>();

	public void addType(final String type) {
		this.type.add(type);
	}

	public void addTransportMode(final String mode) {
		transportMode.add(mode);
	}

	public void addAdditionalAid(final String aid) {
		additionalAids.add(aid);
	}

	public void addMobilityAid(final String aid) {
		mobilityAids.add(aid);
	}

	public void setWinterService(final boolean winterService) {
		this.winterService = winterService;
	}

	public void addNote(final String note) {
		notes.add(note);
	}

	public boolean hasType() {
		return !type.isEmpty();
	}

	public Map<String, Object> build() {
		final var result = new LinkedHashMap<String, Object>();

		if (!type.isEmpty()) {
			result.put("type", List.copyOf(type));
		}
		result.put("transportMode", List.copyOf(transportMode));
		if (!additionalAids.isEmpty()) {
			result.put("additionalAids", List.copyOf(additionalAids));
		}
		if (!mobilityAids.isEmpty()) {
			result.put("mobilityAids", List.copyOf(mobilityAids));
		}
		result.put("isWinterService", winterService);
		if (!notes.isEmpty()) {
			result.put("notes", String.join("; ", notes));
		}

		return result;
	}
}
