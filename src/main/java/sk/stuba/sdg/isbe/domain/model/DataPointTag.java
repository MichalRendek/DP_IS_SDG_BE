package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

public class DataPointTag {
    @Id
    private String uid;
    private String tag;
    private String name;
    private String unit;
    private Long decimal;
    @DBRef
    private List<StoredData> storedData = new ArrayList<>();
    private Long createdAt;
    private boolean deactivated;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getDecimal() {
        return decimal;
    }

    public void setDecimal(Long decimal) {
        this.decimal = decimal;
    }

    public List<StoredData> getStoredData() {
        return storedData;
    }

    public void setStoredData(List<StoredData> storedData) {
        this.storedData = storedData;
    }

    public void addStoredData(StoredData storedData) {
        this.storedData.add(storedData);
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isValid() {
        return this.getName() != null &&
                this.getUnit() != null;
    }
}
