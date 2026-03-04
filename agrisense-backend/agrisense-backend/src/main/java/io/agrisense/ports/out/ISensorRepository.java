package io.agrisense.ports.out;

import java.util.List;

import io.agrisense.domain.model.Sensor;

public interface ISensorRepository {
    Sensor findById(Long id);
    Sensor save(Sensor sensor);
    List<Sensor> findAll();
    void delete(Sensor sensor);
}