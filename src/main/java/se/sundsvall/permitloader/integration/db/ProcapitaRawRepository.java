package se.sundsvall.permitloader.integration.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.permitloader.integration.db.model.ProcapitaRawEntity;

public interface ProcapitaRawRepository extends JpaRepository<ProcapitaRawEntity, Long> {

	List<ProcapitaRawEntity> findByPersonalNumberIsNotNullAndPartyIdIsNull();

	List<ProcapitaRawEntity> findByPartyIdIsNotNullAndPartyAssetIdIsNull();
}
