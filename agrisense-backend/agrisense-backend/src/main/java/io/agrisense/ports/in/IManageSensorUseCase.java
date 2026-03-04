package io.agrisense.ports.in;

import io.agrisense.domain.model.Sensor;
import java.util.List;

public interface IManageSensorUseCase {
    Sensor createSensor(Sensor sensor);
    List<Sensor> getAllSensors();
    Sensor getSensorById(Long id);
    Sensor updateSensor(Long id, Sensor sensor);
    void deleteSensor(Long id);
}