package se.sundsvall.permitloader.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonValueBuilderTest {

	@Test
	void testBuildWithAllValues() {
		final var builder = new JsonValueBuilder();
		builder.addType("arbetsresor");
		builder.addType("privat_fritid");
		builder.addTransportMode("buss");
		builder.addTransportMode("tag");
		builder.addAdditionalAid("baksate");
		builder.addMobilityAid("rollator");
		builder.setWinterService(true);
		builder.addNote("Enstaka resa");

		final var result = builder.build();

		assertThat(result)
			.containsEntry("type", List.of("arbetsresor", "privat_fritid"))
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
	void testBuildWithSingleType() {
		final var builder = new JsonValueBuilder();
		builder.addType("privat_fritid");

		final var result = builder.build();

		assertThat(result)
			.hasSize(2)
			.containsEntry("type", List.of("privat_fritid"))
			.containsEntry("isWinterService", false);
	}

	@Test
	void testBuildWithMultipleTypes() {
		final var builder = new JsonValueBuilder();
		builder.addType("arbetsresor");
		builder.addType("privat_fritid");
		builder.addType("generellt_tillstand");

		final var result = builder.build();

		assertThat(result)
			.hasSize(2)
			.containsEntry("type", List.of("arbetsresor", "privat_fritid", "generellt_tillstand"))
			.containsEntry("isWinterService", false);
	}

	@Test
	void testDuplicateTypesAreIgnored() {
		final var builder = new JsonValueBuilder();
		builder.addType("arbetsresor");
		builder.addType("arbetsresor");

		final var result = builder.build();

		assertThat(result).containsEntry("type", List.of("arbetsresor"));
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
