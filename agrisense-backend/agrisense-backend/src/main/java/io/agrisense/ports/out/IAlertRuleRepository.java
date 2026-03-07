package io.agrisense.ports.out;

import java.util.List;

import io.agrisense.domain.model.AlertRule;

public interface IAlertRuleRepository {
    AlertRule save(AlertRule alertRule);
    AlertRule findById(Long id);
    List<AlertRule> findActiveBySensorId(Long sensorId);
    void delete(AlertRule alertRule);
}