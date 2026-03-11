package io.agrisense.adapter.in.web.dto;

import java.time.Instant;

public class MeasurementResponse {
    private Long id;
    private Long sensorId;
    private Instant timestamp;
    private double value;
    private String unit;
    private HateoasLinks _links;

    public MeasurementResponse() {
    }

    public MeasurementResponse(Long id, Long sensorId, Instant timestamp, double value, String unit) {
        this.id = id;
        this.sensorId = sensorId;
        this.timestamp = timestamp;
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

    public HateoasLinks get_links() {
        return _links;
    }

    public void set_links(HateoasLinks _links) {
        this._links = _links;
    }
}
