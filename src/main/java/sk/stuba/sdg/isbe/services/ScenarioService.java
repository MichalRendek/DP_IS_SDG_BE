package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Scenario;

import java.util.List;

public interface ScenarioService {

    Scenario createScenario(Scenario scenario);

    Scenario upsertScenario(Scenario scenario);

    void validateScenario(Scenario scenario);

    void validateScenarioId(String scenarioId);

    List<Scenario> getScenariosAssociatedWithDevice(String deviceID);

    List<Scenario> getScenarios();

    List<Scenario> getActiveScenarios();

    Scenario getScenarioById(String scenarioId);

    List<Scenario> getScenarioByDeviceAndTag(String deviceId, String tag);

    Scenario muteScenario(String scenarioId, Integer minutes);

    List<Scenario> getScenariosWithMessage();

    Scenario resolveScenario(String scenarioId, String scenarioLevelEnum);

    void storeScenarioJobTriggers();

    Scenario setScenarioActiveAtHour(String scenarioId, List<Integer> hours);

    Scenario setScenarioActiveAtDay(String scenarioId, List<Integer> days);

    Scenario removeScenarioActiveAtHour(String scenarioId);

    Scenario removeScenarioActiveAtDay(String scenarioId);

    Scenario editScenario(Scenario scenario);

    Scenario deleteScenario(String scenarioId);
}
