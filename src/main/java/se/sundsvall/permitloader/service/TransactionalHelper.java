package se.sundsvall.permitloader.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.permitloader.integration.db.ProcapitaRawRepository;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class TransactionalHelper {

	private final ProcapitaRawRepository repository;

	public TransactionalHelper(final ProcapitaRawRepository repository) {
		this.repository = repository;
	}

	@Transactional(propagation = REQUIRES_NEW)
	public void saveEntity(final ProcapitaRawEntity entity) {
		repository.save(entity);
	}
}
