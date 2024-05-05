package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedScenario;
import sk.stuba.sdg.isbe.services.StoredResolvedScenarioService;

import java.util.List;

@RestController
@RequestMapping("api/storedResolvedScenario")
public class StoredResolvedScenarioController {

    @Autowired
    StoredResolvedScenarioService storedResolvedScenarioService;

    @GetMapping("/job")
    @Operation(summary = "Get all Stored data for resolved Scenarios by action type Job.")
    public List<StoredResolvedScenario> getStoredResolvedScenarioDataByJobAction() {
        return this.storedResolvedScenarioService.getStoredResolvedScenarioDataByJobAction();
    }

    @GetMapping("/message")
    @Operation(summary = "Get all Stored data for resolved Scenarios by action type Message.")
    public List<StoredResolvedScenario> getStoredResolvedScenarioDataByMessageAction() {
        return this.storedResolvedScenarioService.getStoredResolvedScenarioDataByMessageAction();
    }
}
