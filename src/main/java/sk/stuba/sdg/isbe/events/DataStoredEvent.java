package sk.stuba.sdg.isbe.events;

import org.springframework.context.ApplicationEvent;
import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.time.Instant;
import java.util.List;

public class DataStoredEvent extends ApplicationEvent {
    private final List<StoredData> storedData;

    private final String deviceId;

    public DataStoredEvent(Object source, List<StoredData> storedData, String deviceId) {
        super(source);
        this.storedData = storedData;
        this.deviceId = deviceId;
    }

    public List<StoredData> getStoredData() {
        return storedData;
    }

    public String getDeviceId() {
        return deviceId;
    }

}