package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.services.DeviceTemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/deviceTemplate")
public class DeviceTemplateController {

    @Autowired
    private DeviceTemplateService deviceTemplateService;

    @Operation(summary = "Create a new device template")
    @PostMapping("/create")
    public Device createDeviceTemplate(@Valid @RequestBody Device device) {
        return deviceTemplateService.createDeviceTemplate(device);
    }

    @Operation(summary = "Get all device templates")
    @GetMapping("/all")
    public List<Device> getAllDeviceTemplates() {
        return deviceTemplateService.getDeviceTemplates();
    }

    @Operation(summary = "Update a device template by ID")
    @PutMapping("/update/{deviceId}")
    public Device updateDeviceTemplate(@PathVariable String deviceId, @RequestBody Device device) {
        return deviceTemplateService.updateDeviceTemplate(deviceId, device);
    }

    @Operation(summary = "Get a device template by its ID")
    @GetMapping("/{deviceId}")
    public Device getDeviceTemplateById(@PathVariable String deviceId) {
        return deviceTemplateService.getDeviceTemplateById(deviceId);
    }

    @Operation(summary = "Delete a device template by its ID")
    @DeleteMapping("/delete/{deviceId}")
    public ResponseEntity<Device> deleteDeviceTemplate(@PathVariable String deviceId) {
        return deviceTemplateService.deleteDeviceTemplate(deviceId);
    }
}
