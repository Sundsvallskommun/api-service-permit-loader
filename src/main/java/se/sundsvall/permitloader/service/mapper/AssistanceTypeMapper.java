package se.sundsvall.permitloader.service.mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AssistanceTypeMapper {

	private static final Logger LOG = LoggerFactory.getLogger(AssistanceTypeMapper.class);

	private AssistanceTypeMapper() {}

	private static final Map<String, String> TYPE_MAP = Map.of(
		"Arbetsresor", "arbetsresor",
		"Privatresor", "privat_fritid",
		"Generellt tillstånd", "generellt_tillstand");

	private static final Map<String, List<String>> TRANSPORT_MODE_MAP = Map.of(
		"Buss", List.of("buss"),
		"Flyg", List.of("flyg"),
		"Tåg", List.of("tag"),
		"Bil", List.of("personbilstaxi"),
		"Specialfordon", List.of("rullstolstaxi", "fordon_hogt_insteg"));

	private static final Map<String, List<String>> ADDITIONAL_AIDS_MAP = Map.of(
		"Baksäte", List.of("baksate"),
		"Framsäte", List.of("framsate"),
		"Ensamåkning", List.of("ensamakning"),
		"Hämta/Lämna bostaden", List.of("hamta_lamnas"),
		"Färdtjänst med ledsagare", List.of("ledsagare"),
		"Riksfärdtjänst med ledsagare", List.of("ledsagare"));

	private static final Map<String, List<String>> MOBILITY_AIDS_MAP = Map.ofEntries(
		Map.entry("Rollator", List.of("rollator")),
		Map.entry("Kryckor/käpp", List.of("krycka_kapp_stavar")),
		Map.entry("Stavar", List.of("krycka_kapp_stavar")),
		Map.entry("Ledarhund", List.of("ledarhund")),
		Map.entry("Vagn", List.of("vagn")),
		Map.entry("Syrgas", List.of("syrgas")),
		Map.entry("Elmoped", List.of("elscooter_elmoped")),
		Map.entry("Elrullstol", List.of("elrullstol")),
		Map.entry("Rullstol", List.of("hopfallbar_rullstol", "komfortrullstol")));

	private static final Map<String, String> NOTES_MAP = Map.of(
		"Anslutningsresa m specialfordon", "Anslutningsresa med specialfordon",
		"Enstaka resa", "Enstaka resa");

	private static final String WINTER_SERVICE = "Vinterfärdtjänst";

	// Values that determine permit type - handled by PermitMapper via permit_group column
	private static final List<String> PERMIT_TYPE_VALUES = List.of(
		"Färdtjänst med ledsagare",
		"Färdtjänst utan ledsagare",
		"Riksfärdtjänst med ledsagare",
		"Riksfärdtjänst utan ledsagare");

	public static void applyAssistanceType(final String assistanceType, final JsonValueBuilder builder) {
		if (assistanceType == null) {
			return;
		}

		final var value = assistanceType.trim();
		boolean matched = false;

		matched |= applyIfPresent(TYPE_MAP, value, builder::setType);
		matched |= applyListIfPresent(TRANSPORT_MODE_MAP, value, builder::addTransportMode);
		matched |= applyListIfPresent(ADDITIONAL_AIDS_MAP, value, builder::addAdditionalAid);
		matched |= applyListIfPresent(MOBILITY_AIDS_MAP, value, builder::addMobilityAid);
		matched |= applyIfPresent(NOTES_MAP, value, builder::addNote);
		matched |= applyWinterService(value, builder);
		matched |= PERMIT_TYPE_VALUES.contains(value);

		if (!matched) {
			LOG.warn("Unknown assistance_type value: '{}'", value);
		}
	}

	private static boolean applyIfPresent(final Map<String, String> map, final String key, final Consumer<String> setter) {
		return Optional.ofNullable(map.get(key))
			.map(v -> {
				setter.accept(v);
				return true;
			})
			.orElse(false);
	}

	private static boolean applyListIfPresent(final Map<String, List<String>> map, final String key, final Consumer<String> adder) {
		return Optional.ofNullable(map.get(key))
			.map(values -> {
				values.forEach(adder);
				return true;
			})
			.orElse(false);
	}

	private static boolean applyWinterService(final String value, final JsonValueBuilder builder) {
		if (WINTER_SERVICE.equals(value)) {
			builder.setWinterService(true);
			return true;
		}
		return false;
	}
}
