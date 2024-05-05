package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Module;
import sk.stuba.sdg.isbe.services.ModuleService;

import java.util.List;

@RestController
@RequestMapping("api/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Operation(summary = "Create a new module")
    @PostMapping("/create")
    public Module createModule(@Valid @RequestBody Module module) {
        return moduleService.createModule(module);
    }

    @Operation(summary = "Update an existing module by ID")
    @PutMapping("/update/{moduleId}")
    public Module updateModule(@PathVariable String moduleId, @RequestBody Module module) {
        return moduleService.updateModule(moduleId, module);
    }

    @Operation(summary = "Get a module by its ID")
    @GetMapping("/{moduleId}")
    public Module getModuleById(@PathVariable String moduleId) {
        return moduleService.getModuleById(moduleId);
    }

    @Operation(summary = "Delete a module by its ID")
    @DeleteMapping("/delete/{moduleId}")
    public ResponseEntity<Module> deleteModule(@PathVariable String moduleId) {
        return moduleService.deleteModule(moduleId);
    }

    @Operation(summary = "Add a device to a module")
    @PutMapping("/addDevice/{moduleId}/{deviceId}")
    public ResponseEntity<Device> addDeviceToModule(@PathVariable String moduleId, @PathVariable String deviceId) {
        return moduleService.addDeviceToModule(moduleId, deviceId);
    }

    @Operation(summary = "Remove a device from a module")
    @DeleteMapping("/removeDevice/{moduleId}/{deviceId}")
    public ResponseEntity<Device> deleteDeviceFromModule(@PathVariable String moduleId, @PathVariable String deviceId) {
        return moduleService.deleteDeviceFromModule(moduleId, deviceId);
    }

    @Operation(summary = "Get all devices in a module")
    @GetMapping("/getDevices/{moduleId}")
    public List<Device> getDevices(@PathVariable String moduleId) {
        return moduleService.getDevices(moduleId);
    }
}
