package io.agrisense.domain.service;

import java.util.List;

import io.agrisense.domain.model.AlertRule;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.in.IManageAlertRuleUseCase;
import io.agrisense.ports.out.IAlertRuleRepository;
import io.agrisense.ports.out.ISensorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AlertRuleService implements IManageAlertRuleUseCase {
    
    private final ISensorRepository sensorRepository;
    private final IAlertRuleRepository alertRuleRepository;
    
    public AlertRuleService(ISensorRepository sensorRepository, 
                           IAlertRuleRepository alertRuleRepository) {
        this.sensorRepository = sensorRepository;
        this.alertRuleRepository = alertRuleRepository;
    }

    @Override
    @Transactional
    public AlertRule createRule(Long sensorId, AlertRule rule) {
        if (sensorId == null) {
            throw new IllegalArgumentException("Sensor ID cannot be null");
        }
        if (rule == null) {
            throw new IllegalArgumentException("Alert rule cannot be null");
        }
        Sensor sensor = sensorRepository.findById(sensorId);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " not found");
        }
        if (sensor.getFieldId() == null) {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " has no field assigned");
        }
        rule.setSensorId(sensorId);
        return alertRuleRepository.save(rule);
    }

    @Override
    public List<AlertRule> getActiveRules(Long sensorId) {
        if (sensorId == null) {
            throw new IllegalArgumentException("Sensor ID cannot be null");
        }
        Sensor sensor = sensorRepository.findById(sensorId);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " not found");
        }
        return alertRuleRepository.findActiveBySensorId(sensorId);
    }

    @Override
    @Transactional
    public AlertRule updateRule(Long ruleId, AlertRule updatedRule) {
        if (ruleId == null) {
            throw new IllegalArgumentException("Rule ID cannot be null");
        }
        if (updatedRule == null) {
            throw new IllegalArgumentException("Updated rule cannot be null");
        }

        AlertRule existing = alertRuleRepository.findById(ruleId);
        if (existing == null) {
            throw new IllegalArgumentException("Alert rule with ID " + ruleId + " not found");
        }

        existing.setCondition(updatedRule.getCondition());
        existing.setThreshold(updatedRule.getThreshold());
        existing.setRuleName(updatedRule.getRuleName());
        
        return alertRuleRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("Rule ID cannot be null");
        }

        AlertRule rule = alertRuleRepository.findById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("Alert rule with ID " + ruleId + " not found");
        }

        alertRuleRepository.delete(rule);
    }
}
