package io.agrisense.ports.in;

public interface IProcessMeasurementUseCase {
    void processMeasurement(Long sensorId, double value, String unit);
}