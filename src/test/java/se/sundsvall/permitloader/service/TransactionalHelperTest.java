package se.sundsvall.permitloader.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.permitloader.integration.db.ProcapitaRawRepository;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionalHelperTest {

	@Mock
	private ProcapitaRawRepository repositoryMock;

	@InjectMocks
	private TransactionalHelper transactionalHelper;

	@Test
	void testSaveEntity() {
		final var entity = new ProcapitaRawEntity();

		transactionalHelper.saveEntity(entity);

		verify(repositoryMock).save(entity);
	}
}
