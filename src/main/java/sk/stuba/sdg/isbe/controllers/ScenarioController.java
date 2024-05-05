package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Scenario;
import sk.stuba.sdg.isbe.services.ScenarioService;

import java.util.List;

@RestController
@RequestMapping("api/scenarios")
public class ScenarioController {

    @Autowired
    ScenarioService scenarioService;

    @GetMapping("/all")
    @Operation(summary = "Get all Scenarios.")
    public List<Scenario> getScenarios() {
        return this.scenarioService.getScenarios();
    }

    @GetMapping("/active")
    @Operation(summary = "Get only active Scenarios.")
    public List<Scenario> getActiveScenarios() {
        return this.scenarioService.getActiveScenarios();
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create new Scenario.")
    public Scenario createScenario(@Valid @RequestBody Scenario scenario) {
        return this.scenarioService.createScenario(scenario);
    }

    @GetMapping(value = "/getScenarioById/{scenarioId}")
    @Operation(summary = "Get Scenario by id.")
    public Scenario getScenarioById(@PathVariable("scenarioId") String scenarioId) {
        return this.scenarioService.getScenarioById(scenarioId);
    }

    @GetMapping(value = "/getScenarioByDeviceIdAndTag/{deviceId}/{tag}")
    @Operation(summary = "Get Scenario by DeviceId and Tag (Tag in DataPointTag).")
    public List<Scenario> getScenarioByDeviceAndTag(@PathVariable("deviceId") String deviceId, @PathVariable("tag") String tag) {
        return this.scenarioService.getScenarioByDeviceAndTag(deviceId, tag);
    }

    @GetMapping(value = "/getScenarioByDeviceId/{deviceId}")
    @Operation(summary = "Get Scenario by associated Device id.")
    public List<Scenario> getScenarioByDevice(@PathVariable("deviceId") String id) {
        return this.scenarioService.getScenariosAssociatedWithDevice(id);
    }

    @PutMapping(value = "/edit")
    @Operation(summary = "Edit existing Scenario.")
    public Scenario editScenario(@Valid @RequestBody Scenario scenario) {
        return this.scenarioService.editScenario(scenario);
    }

    @Operation(summary = "Delete Scenario by uid.")
    @DeleteMapping("/delete/{scenarioId}")
    public Scenario deleteScenario(@PathVariable("scenarioId") String scenarioId) {
        return this.scenarioService.deleteScenario(scenarioId);
    }

    @PostMapping(value = "/mute/{scenarioId}/{minutes}")
    @Operation(summary = "Mute Scenario evaluation for certain time - minutes.")
    public Scenario muteScenario(@PathVariable("scenarioId") String scenarioId, @PathVariable("minutes") Integer minutes) {
        return this.scenarioService.muteScenario(scenarioId, minutes);
    }

    @GetMapping(value = "/getScenariosWithMessage")
    @Operation(summary = "Get all Scenarios with some message for user - triggered Scenarios.")
    public List<Scenario> getScenariosWithMessage() {
        return this.scenarioService.getScenariosWithMessage();
    }

    @PutMapping(value = "/resolveScenario/{scenarioId}/{scenarioLevel}")
    @Operation(summary = "Resolve certain Scenario.")
    public Scenario resolveScenario(@PathVariable("scenarioId") String scenarioId, @PathVariable("scenarioLevel") String scenarioLevel) {
        return this.scenarioService.resolveScenario(scenarioId, scenarioLevel);
    }

    @GetMapping(value = "/storeScenarioJobTriggers")
    @Operation(summary = "Store data about Scenario trigger related to run (reset) job.")
    public void storeScenarioJobTriggers() {
        this.scenarioService.storeScenarioJobTriggers();
    }

    @PutMapping(value = "/setScenarioActiveAtHour/{scenarioId}/{activeHours}")
    @Operation(summary = "Set active hours (hours when Scenario is evaluating) for Scenario.")
    public void setScenarioActiveAtHour(@PathVariable("scenarioId") String scenarioId, @PathVariable("activeHours") List<Integer> activeHours) {
        this.scenarioService.setScenarioActiveAtHour(scenarioId, activeHours);
    }

    @PutMapping(value = "/setScenarioActiveAtDay/{scenarioId}/{activeDays}")
    @Operation(summary = "Set active days (days when Scenario is evaluating) for Scenario.")
    public void setScenarioActiveAtDay(@PathVariable("scenarioId") String scenarioId, @PathVariable("activeDays") List<Integer> activeDays) {
        this.scenarioService.setScenarioActiveAtDay(scenarioId, activeDays);
    }

    @PutMapping(value = "/removeScenarioActiveAtHour/{scenarioId}")
    @Operation(summary = "Remove active hours (hours when Scenario is evaluating) for Scenario.")
    public void removeScenarioActiveAtHour(@PathVariable("scenarioId") String scenarioId) {
        this.scenarioService.removeScenarioActiveAtHour(scenarioId);
    }

    @PutMapping(value = "/removeScenarioActiveAtDay/{scenarioId}")
    @Operation(summary = "Remove active days (days when Scenario is evaluating) for Scenario.")
    public void removeScenarioActiveAtDay(@PathVariable("scenarioId") String scenarioId) {
        this.scenarioService.removeScenarioActiveAtDay(scenarioId);
    }
}
