package io.agrisense.ports.out;

import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.PagedResult;
import java.time.Instant;
import java.util.List;

public interface IMeasurementRepository {
    Measurement save(Measurement measurement);

    /**
     * Query measurements with filters and pagination
     */
    PagedResult<Measurement> findByFilters(Long fieldId, Long sensorId, Instant from, Instant to, int page, int size);

    /**
     * Count total measurements matching filters
     */
    long countByFilters(Long fieldId, Long sensorId, Instant from, Instant to);
}