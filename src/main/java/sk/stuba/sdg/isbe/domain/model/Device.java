package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
public class Device {
    @Id
    private String uid;
    @DBRef
    private User user;
    @DBRef
    private List<User> sharedUsers;
    private String name;
    private String mac;
    private DeviceTypeEnum type;
    private String version;
    private String firmware;
    @DBRef
    private List<Job> jobs = new ArrayList<>();
    @DBRef
    private List<DataPointTag> dataPointTags = new ArrayList<>();
    private Long responseTime = 10L;
    private Long lastResponse;
    private Long addTime;
    private Long initExpireTime;
    private String initApiKey;
    private boolean deactivated;

    public Device() {}

    public Device(String name, String mac, DeviceTypeEnum type) {
        this.name = name;
        this.mac = mac;
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(List<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public DeviceTypeEnum getType() {
        return type;
    }

    public void setType(DeviceTypeEnum type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public List<DataPointTag> getDataPointTags() {
        return dataPointTags;
    }

    public void setDataPointTags(List<DataPointTag> dataPointTags) {
        this.dataPointTags = dataPointTags;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public Long getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Long lastResponse) {
        this.lastResponse = lastResponse;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Long getInitExpireTime() {
        return initExpireTime;
    }

    public void setInitExpireTime(Long initExpireTime) {
        this.initExpireTime = initExpireTime;
    }

    public String getInitApiKey() {
        return initApiKey;
    }

    public void setInitApiKey(String initApiKey) {
        this.initApiKey = initApiKey;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Device) {
            Device device = (Device) obj;
            return device.getUid().equals(this.getUid());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getUid().hashCode();
    }

}
