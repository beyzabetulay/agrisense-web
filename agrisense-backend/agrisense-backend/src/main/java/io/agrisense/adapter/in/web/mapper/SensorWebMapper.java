package io.agrisense.adapter.in.web.mapper;

import io.agrisense.adapter.in.web.dto.CreateSensorRequest;
import io.agrisense.adapter.in.web.dto.SensorResponse;
import io.agrisense.domain.model.ESensorStatus;
import io.agrisense.domain.model.Sensor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SensorWebMapper {

    public Sensor toDomain(CreateSensorRequest request) {
        if (request == null)
            return null;
        Sensor sensor = new Sensor(
                request.getName(),
                request.getType(),
                request.getApiKey(),
                request.getFieldId());
        // Default status to ACTIVE if not provided
        sensor.setStatus(request.getStatus() != null ? request.getStatus() : ESensorStatus.ACTIVE);
        return sensor;
    }

    public SensorResponse toResponse(Sensor sensor) {
        if (sensor == null)
            return null;
        return new SensorResponse(
                sensor.getId(),
                sensor.getName(),
                sensor.getType(),
                sensor.getStatus(),
                sensor.getApiKey(),
                sensor.getFieldId());
    }

    public List<SensorResponse> toResponseList(List<Sensor> sensors) {
        return sensors.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}