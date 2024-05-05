package sk.stuba.sdg.isbe.services.impl;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.ScenarioActionType;
import sk.stuba.sdg.isbe.domain.enums.ScenarioLevelEnum;
import sk.stuba.sdg.isbe.domain.model.Scenario;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedScenario;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.ScenarioRepository;
import sk.stuba.sdg.isbe.repositories.StoredResolvedScenarioRepository;
import sk.stuba.sdg.isbe.services.ScenarioService;
import sk.stuba.sdg.isbe.utilities.ScenarioLevelUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private StoredResolvedScenarioRepository storedResolvedScenarioRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Scenario createScenario(Scenario scenario) {

        Optional<Scenario> scenarioInDb = scenarioRepository.getScenarioByName(scenario.getName());

        if (scenarioInDb.isPresent()) {
            throw new EntityExistsException("Scenario with this name already exists.");
        }

        validateScenario(scenario);
        scenario.setCreatedAt(Instant.now().toEpochMilli());
        scenario.setMutedUntil(null);
        scenario.setForTimeCountingActivatedAt(null);

        return upsertScenario(scenario);
    }

    @Override
    public List<Scenario> getScenariosAssociatedWithDevice(String deviceID) {

        if (deviceID == null || deviceID.isEmpty()) {
            throw new InvalidOperationException("deviceID needs to be set for this operation.");
        }

        List<Scenario> scenarios = scenarioRepository.getScenarioByDevicesContainingAndDeactivated(deviceID, false);

        if (scenarios.isEmpty()) {
            throw new EntityExistsException("Scenario associated with deviceID: " + deviceID + " does not exists.");
        }
        return scenarios;
    }

    @Override
    public List<Scenario> getScenarios() {

        return scenarioRepository.findAll();
    }

    @Override
    public List<Scenario> getActiveScenarios() {

        return scenarioRepository.getScenarioByDeactivated(false);
    }

    @Override
    public Scenario getScenarioById(String scenarioId) {

        validateScenarioId(scenarioId);

        Scenario scenario = scenarioRepository.getScenarioById(scenarioId);

        if (scenario == null) {
            throw new NotFoundCustomException("Scenario with ID: '" + scenarioId + "' was not found!");
        }

        return scenario;
    }

    @Override
    public List<Scenario> getScenarioByDeviceAndTag(String deviceId, String tag) {

        return scenarioRepository.getScenarioByDeviceAndTag(deviceId, tag);
    }

    @Override
    public Scenario editScenario(Scenario scenario) {

        validateScenarioId(scenario.getId());

        Scenario existingScenario = scenarioRepository.findById(scenario.getId())
                .orElseThrow(() -> new InvalidEntityException("Scenario not found."));

        validateScenario(scenario);

        existingScenario.setName(scenario.getName());
        existingScenario.getDevices().clear();
        existingScenario.getDevices().addAll(scenario.getDevices());
        existingScenario.setDeactivated(scenario.getDeactivated());
        existingScenario.getDeviceAndTag().clear();
        existingScenario.getDeviceAndTag().putAll(scenario.getDeviceAndTag());
        existingScenario.setRules(scenario.getRules());
        existingScenario.setAlreadyTriggered(scenario.getAlreadyTriggered());
        existingScenario.setMutedUntil(scenario.getMutedUntil());
        existingScenario.getForTimeCountingActivatedAt().clear();
        existingScenario.getForTimeCountingActivatedAt().putAll(scenario.getForTimeCountingActivatedAt());
        existingScenario.getMessageMultiplicityCounter().clear();
        existingScenario.getMessageMultiplicityCounter().putAll(scenario.getMessageMultiplicityCounter());
        existingScenario.getMessageAndTriggerTime().clear();
        existingScenario.getMessageAndTriggerTime().putAll(scenario.getMessageAndTriggerTime());
        existingScenario.getJobAndTriggerTime().clear();
        existingScenario.getJobAndTriggerTime().putAll(scenario.getJobAndTriggerTime());
        existingScenario.getActiveAtDay().clear();
        existingScenario.getActiveAtDay().addAll(scenario.getActiveAtDay());
        existingScenario.getActiveAtHour().clear();
        existingScenario.getActiveAtHour().addAll(scenario.getActiveAtHour());

        return upsertScenario(existingScenario);
    }

    @Override
    public Scenario deleteScenario(String scenarioId) {

        validateScenarioId(scenarioId);

        Scenario scenarioToDelete = getScenarioById(scenarioId);
        scenarioToDelete.setDeactivated(true);
        return upsertScenario(scenarioToDelete);
    }

    @Override
    public Scenario muteScenario(String scenarioId, Integer minutes) {

        validateScenarioId(scenarioId);

        if (minutes == null) {
            throw new InvalidOperationException("Mute time for Scenario needs to be defined.");
        }

        Scenario scenarioToMute = getScenarioById(scenarioId);
        long muteTimeConverted = minutes * 60 * 1000;
        scenarioToMute.setMutedUntil(Instant.now().plusMillis(muteTimeConverted).toEpochMilli());

        return upsertScenario(scenarioToMute);
    }

    @Override
    public List<Scenario> getScenariosWithMessage() {

        List<Scenario> scenarios = scenarioRepository.getScenarioByDeactivated(false);

        return scenarios.stream()
                .filter(scenario -> scenario.getMessageAndTriggerTime() != null && !scenario.getMessageAndTriggerTime().isEmpty())
                .toList();
    }

    @Override
    public Scenario resolveScenario(String scenarioId, String scenarioLevel) {

        validateScenarioId(scenarioId);
        Scenario scenarioToResolve = getScenarioById(scenarioId);

        ScenarioLevelEnum scenarioLevelEnum = ScenarioLevelUtils.getScenarioLevelEnum(scenarioLevel);

        StoredResolvedScenario storedResolvedScenario = new StoredResolvedScenario();
        storedResolvedScenario.setScenarioId(scenarioId);
        storedResolvedScenario.setCreatedAt(Instant.now().toEpochMilli());
        storedResolvedScenario.setLevel(scenarioLevelEnum);
        storedResolvedScenario.getMessageAndTriggerTime().putAll(scenarioToResolve.getMessageAndTriggerTime());
        storedResolvedScenario.getMessageMultiplicityCounter().putAll(scenarioToResolve.getMessageMultiplicityCounter());
        storedResolvedScenario.setScenarioActionType(ScenarioActionType.MESSAGE);

        scenarioToResolve.setAlreadyTriggered(false);
        scenarioToResolve.getMessageAndTriggerTime().clear();
        scenarioToResolve.getMessageMultiplicityCounter().clear();

        upsertScenario(scenarioToResolve);

        storedResolvedScenarioRepository.save(storedResolvedScenario);

        return scenarioToResolve;
    }

    @Override
    public void storeScenarioJobTriggers() {

        List<Scenario> scenarios = scenarioRepository.findAllWithJobAndTriggerTime();

        List<StoredResolvedScenario> storedResolvedScenarioList = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            StoredResolvedScenario storedResolvedScenario = new StoredResolvedScenario();
            storedResolvedScenario.setScenarioId(scenario.getId());
            storedResolvedScenario.setCreatedAt(Instant.now().toEpochMilli());
            storedResolvedScenario.setLevel(null);
            storedResolvedScenario.getJobAndTriggerTime().putAll(scenario.getJobAndTriggerTime());
            storedResolvedScenario.setScenarioActionType(ScenarioActionType.JOB);

            storedResolvedScenarioList.add(storedResolvedScenario);

            scenario.setAlreadyTriggered(false);
            scenario.getJobAndTriggerTime().clear();
        }

        if (!storedResolvedScenarioList.isEmpty()) {
            storedResolvedScenarioRepository.saveAll(storedResolvedScenarioList);
            saveAll(scenarios);
        }
    }

    @Override
    public Scenario setScenarioActiveAtHour(String scenarioId, List<Integer> hours) {

        validateScenarioId(scenarioId);
        Scenario scenario = getScenarioById(scenarioId);

        scenario.getActiveAtHour().clear();
        scenario.getActiveAtHour().addAll(hours);

        return upsertScenario(scenario);
    }

    @Override
    public Scenario setScenarioActiveAtDay(String scenarioId, List<Integer> days) {

        validateScenarioId(scenarioId);
        Scenario scenario = getScenarioById(scenarioId);

        scenario.getActiveAtDay().clear();
        scenario.getActiveAtDay().addAll(days);

        return upsertScenario(scenario);
    }

    @Override
    public Scenario removeScenarioActiveAtHour(String scenarioId) {

        validateScenarioId(scenarioId);
        Scenario scenario = getScenarioById(scenarioId);

        scenario.getActiveAtHour().clear();

        return upsertScenario(scenario);
    }

    @Override
    public Scenario removeScenarioActiveAtDay(String scenarioId) {

        validateScenarioId(scenarioId);
        Scenario scenario = getScenarioById(scenarioId);

        scenario.getActiveAtDay().clear();

        return upsertScenario(scenario);
    }

    @Override
    public void validateScenario(Scenario scenario) {

        if (!scenario.hasNonEmptyName()) {
            throw new InvalidEntityException("Scenario name needs to be set correctly.");
        } else if (!scenario.hasNonEmptyDevices()) {
            throw new InvalidEntityException("Scenario devices needs to be set correctly.");
        } else if (scenario.getDeactivated() == null) {
            throw new InvalidEntityException("Scenario activity needs to be set correctly.");
        } else if (!scenario.hasNonEmptyRules()) {
            throw new InvalidEntityException("Scenario rules needs to be set correctly.");
        }
    }

    @Override
    public void validateScenarioId(String scenarioId) {

        if (scenarioId == null || scenarioId.isEmpty()) {
            throw new InvalidEntityException("Scenario id is not valid.");
        }
    }

    public Scenario upsertScenario(Scenario scenario) {
        Query query = new Query(Criteria.where("id").is(scenario.getId()));
        Update update = new Update()
                .set("rules", scenario.getRules())
                .set("name", scenario.getName())
                .set("devices", scenario.getDevices())
                .set("deactivated", scenario.getDeactivated())
                .set("isAlreadyTriggered", scenario.getAlreadyTriggered())
                .set("mutedUntil", scenario.getMutedUntil())
                .set("createdAt", scenario.getCreatedAt())
                .set("forTimeCountingActivatedAt", scenario.getForTimeCountingActivatedAt())
                .set("messageAndTriggerTime", scenario.getMessageAndTriggerTime())
                .set("messageMultiplicityCounter", scenario.getMessageMultiplicityCounter())
                .set("deviceAndTag", scenario.getDeviceAndTag())
                .set("jobAndTriggerTime", scenario.getJobAndTriggerTime())
                .set("activeAtDay", scenario.getActiveAtDay())
                .set("activeAtHour", scenario.getActiveAtHour());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Scenario.class);

        if (updateResult.getMatchedCount() == 0) {
            // if no matching document found, insert a new document
            mongoTemplate.insert(scenario);
        } else {
            // if a matching document is found, update the scenario object with the latest data
            scenario = mongoTemplate.findOne(query, Scenario.class);
        }

        return scenario;
    }

    /**
     * Custom implementation for saveAll Scenarios - with upsert - for sharding.
     */
    public List<Scenario> saveAll(List<Scenario> scenarios) {
        List<Scenario> savedScenarios = new ArrayList<>();

        for (Scenario scenario : scenarios) {
            Scenario savedScenario = upsertScenario(scenario);
            savedScenarios.add(savedScenario);
        }

        return savedScenarios;
    }
}
