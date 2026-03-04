package io.agrisense.domain.model;

import java.time.Instant;

public class Measurement {
    private Long id;
    private Long sensorId;
    private Instant timestamp;
    private double value;
    private String unit;

    public Measurement() {
    }

    public Measurement(Long sensorId, double value, String unit) {
        this.sensorId = sensorId;
        this.timestamp = Instant.now();
        this.value = value;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
