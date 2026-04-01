package se.sundsvall.permitloader.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssistanceTypeMapperTest {

	@Test
	void testTypeMapping() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Arbetsresor", builder);
		assertThat(builder.build()).containsEntry("type", List.of("arbetsresor"));
	}

	@Test
	void testTypeMappingPrivatresor() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Privatresor", builder);
		assertThat(builder.build()).containsEntry("type", List.of("privat_fritid"));
	}

	@Test
	void testTypeMappingGenerelltTillstand() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Generellt tillstånd", builder);
		assertThat(builder.build()).containsEntry("type", List.of("generellt_tillstand"));
	}

	@Test
	void testMultipleTypeMappings() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Arbetsresor", builder);
		AssistanceTypeMapper.applyAssistanceType("Privatresor", builder);
		AssistanceTypeMapper.applyAssistanceType("Generellt tillstånd", builder);
		assertThat(builder.build()).containsEntry("type", List.of("arbetsresor", "privat_fritid", "generellt_tillstand"));
	}

	@Test
	void testTransportModeBuss() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Buss", builder);
		assertThat(builder.build()).containsEntry("transportMode", List.of("buss"));
	}

	@Test
	void testTransportModeSpecialfordon() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Specialfordon", builder);
		assertThat(builder.build()).containsEntry("transportMode", List.of("rullstolstaxi", "fordon_hogt_insteg"));
	}

	@Test
	void testAdditionalAidsBaksate() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Baksäte", builder);
		assertThat(builder.build()).containsEntry("additionalAids", List.of("baksate"));
	}

	@Test
	void testAdditionalAidsLedsagare() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Färdtjänst med ledsagare", builder);
		assertThat(builder.build()).containsEntry("additionalAids", List.of("ledsagare"));
	}

	@Test
	void testMobilityAidsRullstol() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Rullstol", builder);
		assertThat(builder.build()).containsEntry("mobilityAids", List.of("hopfallbar_rullstol", "komfortrullstol"));
	}

	@Test
	void testMobilityAidsKryckor() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Kryckor/käpp", builder);
		assertThat(builder.build()).containsEntry("mobilityAids", List.of("krycka_kapp_stavar"));
	}

	@Test
	void testWinterService() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Vinterfärdtjänst", builder);
		assertThat(builder.build()).containsEntry("isWinterService", true);
	}

	@Test
	void testNotes() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Enstaka resa", builder);
		assertThat(builder.build()).containsEntry("notes", "Enstaka resa");
	}

	@Test
	void testNotesAnslutningsresa() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Anslutningsresa m specialfordon", builder);
		assertThat(builder.build()).containsEntry("notes", "Anslutningsresa med specialfordon");
	}

	@Test
	void testPermitTypeValuesDoNotProduceJsonFields() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Färdtjänst utan ledsagare", builder);
		assertThat(builder.build())
			.hasSize(1)
			.containsEntry("isWinterService", false);
	}

	@Test
	void testNullAssistanceType() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType(null, builder);
		assertThat(builder.build())
			.hasSize(1)
			.containsEntry("isWinterService", false);
	}

	@Test
	void testMultipleAssistanceTypes() {
		final var builder = new JsonValueBuilder();
		AssistanceTypeMapper.applyAssistanceType("Arbetsresor", builder);
		AssistanceTypeMapper.applyAssistanceType("Buss", builder);
		AssistanceTypeMapper.applyAssistanceType("Rollator", builder);
		AssistanceTypeMapper.applyAssistanceType("Vinterfärdtjänst", builder);
		AssistanceTypeMapper.applyAssistanceType("Enstaka resa", builder);
		AssistanceTypeMapper.applyAssistanceType("Färdtjänst med ledsagare", builder);

		final var result = builder.build();

		assertThat(result)
			.containsEntry("type", List.of("arbetsresor"))
			.containsEntry("transportMode", List.of("buss"))
			.containsEntry("mobilityAids", List.of("rollator"))
			.containsEntry("additionalAids", List.of("ledsagare"))
			.containsEntry("isWinterService", true)
			.containsEntry("notes", "Enstaka resa");
	}
}
