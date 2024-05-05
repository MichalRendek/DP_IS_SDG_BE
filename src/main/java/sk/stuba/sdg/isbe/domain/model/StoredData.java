package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;

public class StoredData {

    /**
     *  WARNING: when changing field names, do NOT forget to change field names also in method upsertStoredData()
     */

    @Id
    private String uid;
    private String dataPointTagId;
    private Double value;
    private Long measureAt;
    private Double measureAtDevice;
    private boolean deactivated;
    private String deviceId;
    private String tag;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDataPointTagId() {
        return dataPointTagId;
    }

    public void setDataPointTagId(String dataPointTagId) {
        this.dataPointTagId = dataPointTagId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getMeasureAt() {
        return measureAt;
    }

    public void setMeasureAt(Long measureAt) {
        this.measureAt = measureAt;
    }

    public Double getMeasureAtDevice() {
        return measureAtDevice;
    }

    public void setMeasureAtDevice(Double measureAtDevice) {
        this.measureAtDevice = measureAtDevice;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isValid() {
        return getDataPointTagId() == null && getMeasureAt() == null && getValue() == null;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
