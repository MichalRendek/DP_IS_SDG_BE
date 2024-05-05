package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;

/**
 * This structure holds last measured value of each tag of each device. For example: device1 is measuring temperature
 * and humidity (both tags), this structure then holds last values measured for device1.temperature and device1.humidity
 */
public class LastStoredData {

    @Id
    private String uid;
    private String tag;
    private Double value;
    private String deviceId;


    public String getUid() {
        return uid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
