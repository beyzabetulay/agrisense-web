package io.agrisense.domain.service;

import java.time.Instant;

import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.PagedResult;
import io.agrisense.ports.in.IQueryMeasurementUseCase;
import io.agrisense.ports.out.IMeasurementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MeasurementQueryService implements IQueryMeasurementUseCase {

    private final IMeasurementRepository measurementRepository;

    @Inject
    public MeasurementQueryService(IMeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public PagedResult<Measurement> queryMeasurements(Long fieldId, Long sensorId, Instant from, Instant to, int page,
            int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 50;
        }

        return measurementRepository.findByFilters(fieldId, sensorId, from, to, page, size);
    }
}
