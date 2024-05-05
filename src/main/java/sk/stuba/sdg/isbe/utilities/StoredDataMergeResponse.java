package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.util.Map;

public class StoredDataMergeResponse {
    private Double measureAtDevice;
    private Map<String, StoredData> tagValues;

    public Double getMeasureAtDevice() {
        return measureAtDevice;
    }

    public void setMeasureAtDevice(Double measureAtDevice) {
        this.measureAtDevice = measureAtDevice;
    }

    public Map<String, StoredData> getTagValues() {
        return tagValues;
    }

    public void setTagValues(Map<String, StoredData> tagValues) {
        this.tagValues = tagValues;
    }
}
