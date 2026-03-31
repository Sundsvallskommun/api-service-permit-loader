package se.sundsvall.permitloader.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonValueBuilderTest {

	@Test
	void testBuildWithAllValues() {
		final var builder = new JsonValueBuilder();
		builder.setType("arbetsresor");
		builder.addTransportMode("buss");
		builder.addTransportMode("tag");
		builder.addAdditionalAid("baksate");
		builder.addMobilityAid("rollator");
		builder.setWinterService(true);
		builder.addNote("Enstaka resa");

		final var result = builder.build();

		assertThat(result)
			.containsEntry("type", "arbetsresor")
			.containsEntry("transportMode", List.of("buss", "tag"))
			.containsEntry("additionalAids", List.of("baksate"))
			.containsEntry("mobilityAids", List.of("rollator"))
			.containsEntry("isWinterService", true)
			.containsEntry("notes", "Enstaka resa");
	}

	@Test
	void testBuildWithEmptyBuilder() {
		final var result = new JsonValueBuilder().build();

		assertThat(result)
			.hasSize(1)
			.containsEntry("isWinterService", false);
	}

	@Test
	void testBuildWithOnlyType() {
		final var builder = new JsonValueBuilder();
		builder.setType("privat_fritid");

		final var result = builder.build();

		assertThat(result)
			.hasSize(2)
			.containsEntry("type", "privat_fritid")
			.containsEntry("isWinterService", false);
	}

	@Test
	void testDuplicateTransportModesAreIgnored() {
		final var builder = new JsonValueBuilder();
		builder.addTransportMode("buss");
		builder.addTransportMode("buss");

		final var result = builder.build();

		assertThat(result).containsEntry("transportMode", List.of("buss"));
	}

	@Test
	void testMultipleNotesAreJoined() {
		final var builder = new JsonValueBuilder();
		builder.addNote("Enstaka resa");
		builder.addNote("Anslutningsresa med specialfordon");

		final var result = builder.build();

		assertThat(result).containsEntry("notes", "Enstaka resa; Anslutningsresa med specialfordon");
	}
}
