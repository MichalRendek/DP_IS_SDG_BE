package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.enums.ScenarioActionType;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedScenario;

import java.util.List;

public interface StoredResolvedScenarioRepository extends MongoRepository<StoredResolvedScenario, String> {

    List<StoredResolvedScenario> findByScenarioActionType(ScenarioActionType scenarioActionType);

    List<StoredResolvedScenario> findByCreatedAtLessThan(long timestamp);
}
