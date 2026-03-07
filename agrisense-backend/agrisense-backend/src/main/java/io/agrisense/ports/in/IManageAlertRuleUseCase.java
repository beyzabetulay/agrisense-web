package io.agrisense.ports.in;

import io.agrisense.domain.model.AlertRule;
import java.util.List;

public interface IManageAlertRuleUseCase {
    AlertRule createRule(Long sensorId, AlertRule rule);
    List<AlertRule> getActiveRules(Long sensorId);
    AlertRule updateRule(Long ruleId, AlertRule rule);
    void deleteRule(Long ruleId);
}
