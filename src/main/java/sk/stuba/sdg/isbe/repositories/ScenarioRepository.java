package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sk.stuba.sdg.isbe.domain.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends MongoRepository<Scenario, String> {

    Optional<Scenario> getScenarioByName(String name);

   List<Scenario> getScenarioByDevicesContainingAndDeactivated(String device, boolean deactivated);

    List<Scenario> getScenarioByDeactivated(boolean deactivated);

   Scenario getScenarioById(String id);

   @Query("{'deviceAndTag.?0': ?1}")
   List<Scenario> getScenarioByDeviceAndTag(String device, String tag);

    @Query("{ 'jobAndTriggerTime' : { $exists : true, $ne : {} } }")
    List<Scenario> findAllWithJobAndTriggerTime();

}
