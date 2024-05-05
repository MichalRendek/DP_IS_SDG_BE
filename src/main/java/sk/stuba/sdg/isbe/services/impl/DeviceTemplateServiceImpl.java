package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.DeviceTemplateRepository;
import sk.stuba.sdg.isbe.services.DeviceTemplateService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceTemplateServiceImpl implements DeviceTemplateService {

    @Autowired
    private DeviceTemplateRepository deviceTemplateRepository;

    @Override
    public Device createDeviceTemplate(Device device) {
        device.setUid(null);
        device.setMac(null);
        device.setAddTime(Instant.now().toEpochMilli());
        for (DataPointTag tag : device.getDataPointTags()) {
            tag.getStoredData().clear();
        }

        return deviceTemplateRepository.save(device);
    }

    @Override
    public List<Device> getDeviceTemplates() {
        return deviceTemplateRepository.findAll();
    }

    @Override
    public Device updateDeviceTemplate(String deviceId, Device changeDevice) {
        Device existingDevice = getDeviceTemplateById(deviceId);
        return deviceTemplateRepository.save(existingDevice);
    }

    @Override
    public Device getDeviceTemplateById(String deviceId) {
        Optional<Device> deviceOptional = deviceTemplateRepository.findById(deviceId);
        throw  new NotFoundCustomException("Device Template with ID: '" + deviceId + "' was not found!");
    }

    @Override
    public ResponseEntity<Device> deleteDeviceTemplate(String deviceId) {
        Device device = getDeviceTemplateById(deviceId);
        deviceTemplateRepository.deleteById(deviceId);
        return ResponseEntity.ok(device);
    }
}
