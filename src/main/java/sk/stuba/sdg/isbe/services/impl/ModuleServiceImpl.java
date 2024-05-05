package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Module;
import sk.stuba.sdg.isbe.handlers.exceptions.*;
import sk.stuba.sdg.isbe.repositories.ModuleRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.ModuleService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {

    private static final String EMPTY_STRING = "";

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private DeviceService deviceService;


    @Override
    public Module createModule(Module module) {
        if (module.getName() == null || module.getName().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Module has no name set!");
        }

        module.setCreatedAt(Instant.now().toEpochMilli());
        moduleRepository.save(module);

        return module;
    }

    @Override
    public Module updateModule(String moduleId, Module changeModule) {
        Module module = getModuleById(moduleId);

        if (changeModule == null) {
            throw new InvalidEntityException("Device with changes is null!");
        }

        if (changeModule.getName() != null) {
            module.setName(changeModule.getName());
        }
        if (changeModule.getDevices() != null) {
            module.setDevices(changeModule.getDevices());
        }

        moduleRepository.save(module);

        return module;
    }

    @Override
    public Module getModuleById(String moduleId){
        Optional<Module> optionalModule = moduleRepository.findById(moduleId);
        if (optionalModule.isEmpty()) {
            throw new NotFoundCustomException("Module with ID: '" + moduleId + "' was not found!");
        }
        return optionalModule.get();
    }

    @Override
    public ResponseEntity<Module> deleteModule(String moduleId) {
        Optional<Module> moduleToDeleteOptional = moduleRepository.findById(moduleId);

        if (moduleToDeleteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Module moduleToDelete = moduleToDeleteOptional.get();
        moduleRepository.deleteById(moduleId);
        return ResponseEntity.ok(moduleToDelete);
    }

    @Override
    public ResponseEntity<Device> addDeviceToModule(String moduleId, String deviceId){
        Device device = deviceService.getDeviceById(deviceId);

        Module module = getModuleById(moduleId);
        module.getDevices().add(device);

        moduleRepository.save(module);
        return ResponseEntity.ok(device);
    }

    @Override
    public ResponseEntity<Device> deleteDeviceFromModule(String moduleId, String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);
        Module module = getModuleById(moduleId);

        if (!module.getDevices().contains(device)) {
            return ResponseEntity.notFound().build();
        }

        module.getDevices().remove(device);
        moduleRepository.save(module);

        return ResponseEntity.ok(device);
    }

    @Override
    public List<Device> getDevices(String moduleId) {
        List<Device> devices = getModuleById(moduleId).getDevices();

        if (devices.isEmpty()) {
            throw new NotFoundCustomException("Eny devices here!");
        }

        return devices;
    };
}
