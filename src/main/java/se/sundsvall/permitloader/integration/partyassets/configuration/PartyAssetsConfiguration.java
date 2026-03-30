package se.sundsvall.permitloader.integration.partyassets.configuration;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@Import(FeignConfiguration.class)
public class PartyAssetsConfiguration {

	public static final String REGISTRATION_ID = "partyassets";

	@Bean
	FeignBuilderCustomizer partyAssetsFeignBuilderCustomizer(final ClientRegistrationRepository clientRegistrationRepository) {
		return FeignMultiCustomizer.create()
			.withRetryableOAuth2InterceptorForClientRegistration(clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID))
			.composeCustomizersToOne();
	}
}
