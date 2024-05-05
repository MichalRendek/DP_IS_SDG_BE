package sk.stuba.sdg.isbe.domain.model;

public class DataPoint {
    private String tag;
    private Double value;
    private Double measureAt;

    public DataPoint() {
    }

    public DataPoint(String tag, Double value, Double measureAt) {
        this.tag = tag;
        this.value = value;
        this.measureAt = measureAt;
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

    public Double getMeasureAt() {
        return measureAt;
    }

    public void setMeasureAt(Double measureAt) {
        this.measureAt = measureAt;
    }
}
