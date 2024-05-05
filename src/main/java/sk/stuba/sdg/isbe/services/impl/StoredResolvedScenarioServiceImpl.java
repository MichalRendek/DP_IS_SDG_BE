package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.ScenarioActionType;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedScenario;
import sk.stuba.sdg.isbe.repositories.StoredResolvedScenarioRepository;
import sk.stuba.sdg.isbe.services.StoredResolvedScenarioService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StoredResolvedScenarioServiceImpl implements StoredResolvedScenarioService {

    @Autowired
    StoredResolvedScenarioRepository storedResolvedScenarioRepository;

    @Override
    public List<StoredResolvedScenario> getStoredResolvedScenarioDataByJobAction() {
        return storedResolvedScenarioRepository.findByScenarioActionType(ScenarioActionType.JOB);
    }

    @Override
    public List<StoredResolvedScenario> getStoredResolvedScenarioDataByMessageAction() {
        return storedResolvedScenarioRepository.findByScenarioActionType(ScenarioActionType.MESSAGE);
    }

    @Override
    public void removeOldStoredScenarioData() {

        long timeMinusWeek = Instant.now().minus(7, ChronoUnit.DAYS)
                .toEpochMilli();

        List<StoredResolvedScenario> storedResolvedScenarioList = storedResolvedScenarioRepository.findByCreatedAtLessThan(timeMinusWeek);

        if (!storedResolvedScenarioList.isEmpty()) {
            storedResolvedScenarioRepository.deleteAll(storedResolvedScenarioList);
        }
    }
}
