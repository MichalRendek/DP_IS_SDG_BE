package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.StoredResolvedScenario;

import java.util.List;

public interface StoredResolvedScenarioService {

    List<StoredResolvedScenario> getStoredResolvedScenarioDataByJobAction();

    List<StoredResolvedScenario> getStoredResolvedScenarioDataByMessageAction();

    void removeOldStoredScenarioData();
}
