package io.agrisense.domain.service;

import java.util.List;

import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.in.IManageSensorUseCase;
import io.agrisense.ports.out.ISensorRepository;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SensorManagementService implements IManageSensorUseCase {

    private final ISensorRepository sensorRepository;

    public SensorManagementService(ISensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "sensors-cache")
    @CacheInvalidateAll(cacheName = "sensor-by-id")
    public Sensor createSensor(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    @Override
    @CacheResult(cacheName = "sensors-cache")
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @Override
    @CacheResult(cacheName = "sensor-by-id")
    public Sensor getSensorById(Long id) {
        Sensor sensor = sensorRepository.findById(id);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor with ID " + id + " not found.");
        }
        return sensor;
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "sensors-cache")
    @CacheInvalidateAll(cacheName = "sensor-by-id")
    public Sensor updateSensor(Long id, Sensor updatedSensor) {
        Sensor existing = sensorRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Sensor with ID " + id + " not found.");
        }
        existing.setName(updatedSensor.getName());
        existing.setType(updatedSensor.getType());
        existing.setApiKey(updatedSensor.getApiKey());
        if (updatedSensor.getStatus() != null) {
            existing.setStatus(updatedSensor.getStatus());
        }
        return sensorRepository.save(existing);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "sensors-cache")
    @CacheInvalidateAll(cacheName = "sensor-by-id")
    public void deleteSensor(Long id) {
        Sensor sensor = sensorRepository.findById(id);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor with ID " + id + " not found.");
        }
        sensorRepository.delete(sensor);
    }
}