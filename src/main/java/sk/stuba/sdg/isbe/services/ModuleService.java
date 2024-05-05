package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Module;
import sk.stuba.sdg.isbe.domain.model.Device;

import java.util.List;

public interface ModuleService {
    Module createModule(Module module);

    List<Device> getDevices(String moduleId);

    Module updateModule(String moduleId, Module changeModule);

    Module getModuleById(String moduleId);

    ResponseEntity<Module> deleteModule(String moduleId);

    ResponseEntity<Device> addDeviceToModule(String moduleId, String deviceId);

    ResponseEntity<Device> deleteDeviceFromModule(String moduleId, String deviceId);
}
