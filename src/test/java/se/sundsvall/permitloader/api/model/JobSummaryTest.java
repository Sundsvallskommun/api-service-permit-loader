package se.sundsvall.permitloader.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JobSummaryTest {

	@Test
	void testRecordConstructorAndAccessors() {
		final var jobSummary = new JobSummary(10, 7, 3);

		assertThat(jobSummary.totalProcessed()).isEqualTo(10);
		assertThat(jobSummary.successCount()).isEqualTo(7);
		assertThat(jobSummary.errorCount()).isEqualTo(3);
	}

	@Test
	void testEqualsAndHashCode() {
		final var jobSummary1 = new JobSummary(10, 7, 3);
		final var jobSummary2 = new JobSummary(10, 7, 3);
		final var jobSummary3 = new JobSummary(5, 3, 2);

		assertThat(jobSummary1)
			.isEqualTo(jobSummary2)
			.isNotEqualTo(jobSummary3)
			.hasSameHashCodeAs(jobSummary2);
	}

	@Test
	void testToString() {
		final var jobSummary = new JobSummary(10, 7, 3);

		assertThat(jobSummary.toString()).contains("10", "7", "3");
	}
}
