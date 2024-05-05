package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Device;

import java.util.List;

public interface DeviceTemplateService {
    Device createDeviceTemplate(Device device);

    List<Device> getDeviceTemplates();

    Device updateDeviceTemplate(String deviceId, Device changeDevice);

    Device getDeviceTemplateById(String deviceId);

    ResponseEntity<Device> deleteDeviceTemplate(String deviceId);
}
