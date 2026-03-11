package io.agrisense.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateMeasurementRequest {
    
    @NotNull(message = "Sensor ID is required")
    @Positive(message = "Sensor ID must be positive")
    private Long sensorId;
    
    @NotNull(message = "Measurement value is required")
    private Double value;
    
    private String unit;

    public CreateMeasurementRequest() { }

    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}