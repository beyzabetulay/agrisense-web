package io.agrisense.ports.in;

import io.agrisense.domain.model.EAlertStatus;
import io.agrisense.domain.model.PagedResult;
import io.agrisense.domain.model.Alert;

public interface IQueryAlertUseCase {
    PagedResult<Alert> queryAlerts(EAlertStatus status, Integer page, Integer size);
}
