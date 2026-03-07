package io.agrisense.ports.in;

import java.time.Instant;

import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.PagedResult;

public interface IQueryMeasurementUseCase {

    PagedResult<Measurement> queryMeasurements(
            Long fieldId,
            Long sensorId,
            Instant from,
            Instant to,
            int page,
            int size);
}
