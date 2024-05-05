package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.*;
import sk.stuba.sdg.isbe.domain.model.protofiles.*;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.JobStatusService;
import sk.stuba.sdg.isbe.utilities.DevicebuffConverters;
import sk.stuba.sdg.isbe.utilities.JobStatusbuffConverters;

import java.lang.System;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("api/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JobStatusService jobStatusService;

    @Autowired
    private JobService jobService;

    @GetMapping
    public List<Device> getDevices(@AuthenticationPrincipal User user) {
        return this.deviceService.getDevicesByUser(user);
    }

    @GetMapping("/parsing")
    public Page<Device> getDevicesPaging(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "sortBy", required = false, defaultValue = "addTime") String sortBy,
                                         @RequestParam(value = "descending", defaultValue = "false") boolean descending,
                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "rowsPerPage", defaultValue = "10") int rowsPerPage) {

        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, rowsPerPage, sort);

        return this.deviceService.getDevicesByUserPaging(user, pageable);
    }

    @Operation(summary = "Add new device into the system")
    @PostMapping("/create")
    public Device createDevice(@Valid @RequestBody Device device, @AuthenticationPrincipal User user) {
        return this.deviceService.createDevice(device, user);
    }

    @Operation(summary = "Initialize device by mac address in 1 min time window")
    @GetMapping(value = "/initializeDevice/{macAddress}")
    public Device initializeDevice(@PathVariable String macAddress) {
        return deviceService.initializeDevice(macAddress);
    }

    @Operation(summary = "Initialize time of device set to 1 min window")
    @GetMapping(value = "/initExpireTime/{deviceId}")
    public Long initExpireTime(@PathVariable String deviceId) {
        return deviceService.initExpireTime(deviceId);
    }

    @Operation(summary = "Update Device by ID using object")
    @PutMapping("updateDevice/{deviceId}")
    public Device updateDevice(@PathVariable String deviceId, @Valid @RequestBody Device changeDevice) {
        return deviceService.updateDevice(deviceId, changeDevice);
    }

    @Operation(summary = "Delete device by uid")
    @DeleteMapping("delete/{deviceId}")
    public ResponseEntity<Device> deleteDevice(@PathVariable String deviceId) {
        return this.deviceService.deleteDevice(deviceId);
    }

    @GetMapping("/getDeviceById/{deviceId}")
    public Device getDeviceById(@PathVariable String deviceId) {
        return this.deviceService.getDeviceById(deviceId);
    }

    @Operation(summary = "Add job to device base on id inset into list of jobs")
    @PutMapping("addJobToDevice/{deviceId}/{jobId}")
    public ResponseEntity<Job> addJobToDevice(@PathVariable String deviceId, @PathVariable String jobId) {
        return this.deviceService.addJobToDevice(deviceId, jobId);
    }

    @Operation(summary = "Add data point tag to device base on id inset into list of dataPointTags")
    @PutMapping("addDataPointTagToDevice/{deviceId}/{dataPointTagId}")
    public Device addDataPointTagToDevice(@PathVariable String deviceId, @PathVariable String dataPointTagId) {
        return this.deviceService.addDataPointTagToDevice(deviceId, dataPointTagId);
    }

    @Operation(summary = "Json of all jobs assigned to device, if second parameter set to true only pending get")
    @GetMapping(value = "/getAllDeviceJobs/{deviceId}/{pending}")
    public List<Job> getAllDeviceJobs(@PathVariable String deviceId, @PathVariable Boolean pending) {
        if (pending) {
            return this.deviceService.getPendingDeviceJobs(deviceId);
        }
        return this.deviceService.getAllDeviceJobs(deviceId);
    }

    @Operation(summary = "Get all jobs in devices")
    @GetMapping("/getAllJobsInDevices")
    public List<Map<String, Map<String, List<Job>>>> getAllJobsInDevices() {
        return this.deviceService.getAllJobsInDevices();
    }

    @Operation(summary = "Update JobStatus by device uid")
    @PostMapping("/updateJobStatus/{deviceId}/{deviceKey}/{jobStatusId}")
    public ResponseEntity<Device> updateJobStatus(@PathVariable String deviceId, @PathVariable String deviceKey, @PathVariable String jobStatusId, @Valid @RequestBody JobStatus changeJobStatus) {

        if (deviceId == null || deviceId.isEmpty()) {
            return null;
        }

        if (deviceId != null && deviceService.getDeviceByIdAndKey(deviceId, deviceKey) != null) {
            Device device = deviceService.getDeviceById(deviceId);

            if(jobStatusService.updateJobStatus(jobStatusId, changeJobStatus, deviceId, deviceKey) != null) {

                device.setDataPointTags(null);
                device.setUser(null);
                device.setSharedUsers(null);

                return  ResponseEntity.ok(device);
            }
        }
        return null;
    }

    @Operation(summary = "Update JobStatus by device uid in protobuf format")
    @PostMapping("/updateJobStatusProto/{deviceId}/{jobId}")
    public DeviceBuff.Device updateJobStatusProto(@PathVariable String deviceId, @PathVariable String jobId, @Valid @RequestBody JobStatusBuff.JobStatus changeJobStatus) {

        if (deviceId == null || deviceId.isEmpty()) {
            return null;
        }

        if (changeJobStatus == null) {
            return null;
        }

        JobStatus jobStatus = JobStatusbuffConverters.convertToDomainJobStatus(changeJobStatus);
        Device device = deviceService.getDeviceByIdAndKey(deviceId, jobStatus.getDeviceKey());

        device.setLastResponse(Instant.now().toEpochMilli());
        deviceRepository.save(device);

        System.out.println(jobId);
        if (device != null) {
            device.setDataPointTags(null);
            device.setUser(null);
            device.setSharedUsers(null);

            if (Objects.equals(jobId, "0")) {
                return DevicebuffConverters.toProtobufDevice(device);
            }

            if(jobStatusService.updateJobStatus(jobService.getJobById(jobId).getStatus().getUid(), jobStatus, deviceId, jobStatus.getDeviceKey()) != null) {
                return DevicebuffConverters.toProtobufDevice(device);
            }
        }
        return null;
    }

    @Operation(summary = "Add a shared user to a device")
    @PutMapping("/addSharedUser/{deviceId}/{userMail}")
    public ResponseEntity<Device> addSharedUserToDevice(@PathVariable String deviceId, @PathVariable String userMail) {
        Device updatedDevice = deviceService.addSharedUserToDevice(deviceId, userMail);
        return ResponseEntity.ok(updatedDevice);
    }

    @Operation(summary = "Remove a shared user from a device")
    @DeleteMapping("/removeSharedUser/{deviceId}/{userId}")
    public ResponseEntity<Device> removeSharedUserFromDevice(@PathVariable String deviceId, @PathVariable String userId) {
        Device updatedDevice = deviceService.removeSharedUserFromDevice(deviceId, userId);
        return ResponseEntity.ok(updatedDevice);
    }

    @Operation(summary = "Get shared users of a device")
    @GetMapping("/getSharedUsers/{deviceId}")
    public ResponseEntity<List<User>> getSharedUsers(@PathVariable String deviceId) {
        List<User> sharedUsers = deviceService.getSharedUsers(deviceId);
        return ResponseEntity.ok(sharedUsers);
    }

    @Operation(summary = "Get devices shared with a user")
    @GetMapping("/getDevicesSharedWithUser")
    public List<Device> getDevicesSharedWithUser(@AuthenticationPrincipal User user) {
        return deviceService.getDevicesSharedWithUser(user);
    }

    @Operation(summary = "Get status of the device")
    @GetMapping("getDeviceStatus/{deviceId}")
    public String getDeviceStatus(@PathVariable String deviceId) {
        return this.deviceService.getDeviceStatus(deviceId);
    }
}
