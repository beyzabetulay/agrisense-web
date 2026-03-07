package io.agrisense.ports.out;

import io.agrisense.domain.model.Alert;
import io.agrisense.domain.model.EAlertStatus;
import java.util.List;

public interface IAlertRepository {
    Alert save(Alert alert);
    Alert findOpenAlert(Long sensorId, Long ruleId);
    List<Alert> findByStatus(EAlertStatus status, int offset, int limit);
    long countByStatus(EAlertStatus status);
}