package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.User;
import sk.stuba.sdg.isbe.handlers.exceptions.*;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.repositories.UserRepository;
import sk.stuba.sdg.isbe.services.DataPointTagService;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String EMPTY_STRING = "";
    private static final String NONE = "NONE";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private DataPointTagService dataPointTagService;

    @Override
    public Device createDevice(Device device, User owner) {
        if (device.getName() == null || device.getName().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Device has no name set!");
        }
        if (device.getMac() == null || device.getMac().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Device has no mac address set!");
        }
        if (device.getType() == null) {
            throw new InvalidEntityException("Device has no type set!");
        }

        device.setInitExpireTime((long) -1);
        device.setAddTime(Instant.now().toEpochMilli());
        device.setUser(owner);
        deviceRepository.save(device);

        return device;
    }

    @Override
    public Device initializeDevice(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            throw new InvalidOperationException("Mac address is not set!");
        }

        Device device = deviceRepository.findDeviceByMac(macAddress);
        if (device == null) {
            throw new EntityExistsException("Device with MAC: '" + macAddress + "' was not found!");
        }
        if (device.getInitExpireTime() == -1) {
            throw new InvalidEntityException("Device is initialize!");
        }
        if (device.getInitExpireTime() < Instant.now().toEpochMilli()) {
            throw new InvalidEntityException("Device initial time is out of!");
        }

        device.setInitExpireTime((long) -1);
        deviceRepository.save(device);

        device.setDataPointTags(null);
        device.setUser(null);
        device.setSharedUsers(null);
        return device;
    }

    @Override
    public Long initExpireTime(String deviceId) {
        Device device = getDeviceById(deviceId);
        Long time = Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli();
        device.setInitExpireTime(time);

        deviceRepository.save(device);
        return time;
    }

    @Override
    public List<Device> getDevices() {
        return deviceRepository.getDevicesByDeactivatedWithoutDataPointTags(false);
    }

    @Override
    public List<Device> getDevicesByUser(User user) {
        return deviceRepository.getDevicesByUserDeactivatedWithoutDataPointTags(user.getUid(), false);
    }

    @Override
    public Page<Device> getDevicesByUserPaging(User user, Pageable pageable) {
        return deviceRepository.getDevicesByUserDeactivatedWithoutDataPointTagsPaging(user.getUid(), false, pageable);
    }

    @Override
    public Device updateDevice(String deviceId, Device changeDevice) {
        Device device = getDeviceById(deviceId);

        if (changeDevice == null) {
            throw new InvalidEntityException("Device with changes is null!");
        }

        if (changeDevice.getUser() != null) {
            device.setUser(changeDevice.getUser());
        }
        if (changeDevice.getName() != null) {
            device.setName(changeDevice.getName());
        }
        if (changeDevice.getMac() != null) {
            device.setMac(changeDevice.getMac());
        }
        if (changeDevice.getType() != null) {
            device.setType(changeDevice.getType());
        }
        if (changeDevice.getVersion() != null) {
            device.setVersion(changeDevice.getVersion());
        }
        if (changeDevice.getFirmware() != null) {
            device.setFirmware(changeDevice.getFirmware());
        }
        if (changeDevice.getJobs() != null) {
            device.setJobs(changeDevice.getJobs());
        }
        if (changeDevice.getDataPointTags() != null) {
            device.setDataPointTags(changeDevice.getDataPointTags());
        }
        if (changeDevice.getResponseTime() != null) {
            device.setResponseTime(changeDevice.getResponseTime());
        }
        if (changeDevice.getAddTime() != null) {
            device.setAddTime(changeDevice.getAddTime());
        }
        if (changeDevice.getInitExpireTime() != null) {
            device.setInitExpireTime(changeDevice.getInitExpireTime());
        }
        if (changeDevice.getInitApiKey() != null) {
            device.setInitApiKey(changeDevice.getInitApiKey());
        }
        device.setDeactivated(changeDevice.isDeactivated());

        deviceRepository.save(device);

        return device;
    }

    @Override
    public Device getDeviceById(String deviceId){
        Optional<Device> optionalDevice = deviceRepository.getDeviceByUidAndDeactivated(deviceId, false);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Device with ID: '" + deviceId + "' was not found!");
        }
        return optionalDevice.get();
    }

    @Override
    public Device getDeviceByIdAndKey(String deviceId, String deviceKey){
        Optional<Device> optionalDevice = deviceRepository.getDeviceByUidAndInitApiKey(deviceId, deviceKey);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Device with ID: '" + deviceId + "' was not found!");
        }
        return optionalDevice.get();
    }

    @Override
    public ResponseEntity<Device> deleteDevice(String deviceId) {
        Device deviceToDelete = getDeviceById(deviceId);
        deviceToDelete.setDeactivated(true);
        deviceRepository.save(deviceToDelete);
        return ResponseEntity.ok(deviceToDelete);
    }

    @Override
    public ResponseEntity<Job> addJobToDevice(String deviceId, String jobId){
        Job job = jobService.getJobById(jobId);

        Device device = getDeviceById(deviceId);
        device.getJobs().add(job);

        deviceRepository.save(device);
        return ResponseEntity.ok(job);
    }

    @Override
    public Device addDataPointTagToDevice(String deviceId, String dataPointTagId) {
        DataPointTag dataPointTag = dataPointTagService.getDataPointTagById(dataPointTagId);
        
        Device device = getDeviceById(deviceId);
        device.getDataPointTags().add(dataPointTag);

        return deviceRepository.save(device);
    }

    @Override
    public List<Job> getAllDeviceJobs(String deviceId){
        Device device = getDeviceById(deviceId);

        if (device.getJobs().isEmpty()){
            throw new EntityExistsException("No jobs for device already exists!");
        }
        
        return device.getJobs();
    }

    @Override
    public List<Job> getPendingDeviceJobs(String deviceId) {
        return jobService.getAllJobsByStatus(deviceId, JobStatusEnum.JOB_PENDING.name(), NONE, NONE);
    }

    @Override
    public List<Map<String, Map<String, List<Job>>>> getAllJobsInDevices() {
        List<Device> allDevices = deviceRepository.findAll();
        List<Map<String, Map<String, List<Job>>>> result = new ArrayList<>();

        for (Device device : allDevices) {
            Map<String, List<Job>> idAndJobs = new HashMap<>();
            idAndJobs.put(device.getUid(), device.getJobs());

            Map<String, Map<String, List<Job>>> nameAndDetails = new HashMap<>();
            nameAndDetails.put(device.getName(), idAndJobs);

            result.add(nameAndDetails);
        }

        return result;
    }

    // Method to add a shared user to a device
    @Override
    public Device addSharedUserToDevice(String deviceId, String userMail) {
        Device device = getDeviceById(deviceId);
        User userToAdd = userRepository.getUserByMail(userMail);

        if(userToAdd == null) {
            throw new NotFoundCustomException("User not found!");
        }

        if(device == null) {
            throw new NotFoundCustomException("Device not found!");
        }

        if (device.getSharedUsers() == null) {
            device.setSharedUsers(new ArrayList<>());
        }

        device.getSharedUsers().add(userToAdd);
        deviceRepository.save(device);

        return device;
    }

    // Method to remove a shared user from a device
    @Override
    public Device removeSharedUserFromDevice(String deviceId, String userId) {
        Device device = getDeviceById(deviceId);
        User userToRemove = userRepository.findById(userId).orElseThrow(() -> new NotFoundCustomException("User not found!"));

        if (device.getSharedUsers() != null) {
            device.getSharedUsers().remove(userToRemove);
            deviceRepository.save(device);
        }

        return device;
    }

    // Method to get shared users of a device
    @Override
    public List<User> getSharedUsers(String deviceId) {
        Device device = getDeviceById(deviceId);
        return device.getSharedUsers();
    }

    @Override
    public List<Device> getDevicesSharedWithUser(User user) {
        List<Device> allDevices = deviceRepository.getDevicesByDeactivatedWithoutDataPointTags(false);
        List<Device> sharedDevices = new ArrayList<>();

        for (Device device : allDevices) {
            List<User> sharedUsers = device.getSharedUsers();
            if (sharedUsers != null && sharedUsers.stream().anyMatch(u -> u.getUid().equals(user.getUid()))) {
                sharedDevices.add(device);
            }
        }
        return sharedDevices;
    }

    @Override
    public String getDeviceStatus(String deviceId) {
        Device device = getDeviceById(deviceId);
        LocalDateTime lastUpdated = LocalDateTime.ofInstant(Instant.ofEpochMilli(device.getLastResponse()), ZoneId.systemDefault());

        if (LocalDateTime.now().minusSeconds(device.getResponseTime()).isAfter(lastUpdated)) {
            throw new DeviceErrorException("Device job last updated at: " + lastUpdated.toString().replace("T", " - ")
                    + ". Device may be disconnected!");
        }
        return lastUpdated.toString().replace("T", " - ");
    }
}
