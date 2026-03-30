package se.sundsvall.permitloader.integration.db.model;

import java.time.LocalDate;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class ProcapitaRawEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(ProcapitaRawEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanToString()));
	}

	@Test
	void testEqualsAndHashCode() {
		final var entity1 = new ProcapitaRawEntity();
		entity1.setId(1L);

		final var entity2 = new ProcapitaRawEntity();
		entity2.setId(1L);

		final var entity3 = new ProcapitaRawEntity();
		entity3.setId(2L);

		assertThat(entity1)
			.isEqualTo(entity2)
			.isNotEqualTo(entity3)
			.isNotEqualTo(null)
			.isNotEqualTo("other")
			.isEqualTo(entity1)
			.hasSameHashCodeAs(entity2);
	}
}
