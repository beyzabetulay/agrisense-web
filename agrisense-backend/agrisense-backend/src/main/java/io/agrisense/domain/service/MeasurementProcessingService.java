package io.agrisense.domain.service;

import java.util.List;

import io.agrisense.domain.model.Alert;
import io.agrisense.domain.model.AlertRule;
import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.in.IProcessMeasurementUseCase;
import io.agrisense.ports.out.IAlertRepository;
import io.agrisense.ports.out.IAlertRuleRepository;
import io.agrisense.ports.out.IMeasurementRepository;
import io.agrisense.ports.out.ISensorRepository;
import io.quarkus.cache.CacheInvalidateAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MeasurementProcessingService implements IProcessMeasurementUseCase {

    private final ISensorRepository sensorRepository;
    private final IMeasurementRepository measurementRepository;
    private final IAlertRuleRepository alertRuleRepository;
    private final IAlertRepository alertRepository;

    public MeasurementProcessingService(ISensorRepository sensorRepository,
            IMeasurementRepository measurementRepository,
            IAlertRuleRepository alertRuleRepository,
            IAlertRepository alertRepository) {
        this.sensorRepository = sensorRepository;
        this.measurementRepository = measurementRepository;
        this.alertRuleRepository = alertRuleRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "open-alerts")
    public void processMeasurement(Long sensorId, double value, String unit) {

        Sensor sensor = sensorRepository.findById(sensorId);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor not found: " + sensorId);
        }

        Measurement measurement = new Measurement(sensorId, value, unit);
        measurementRepository.save(measurement);

        List<AlertRule> activeRules = alertRuleRepository.findActiveBySensorId(sensorId);

        for (AlertRule rule : activeRules) {
            if (rule.isViolated(value)) {
                Alert existingOpenAlert = alertRepository.findOpenAlert(sensorId, rule.getId());

                if (existingOpenAlert == null) {
                    String alertMessage = String.format(
                            "Rule '%s' violated: sensor value %.2f %s",
                            rule.getRuleName(), value, unit);
                    Alert newAlert = new Alert(sensorId, rule.getId(), alertMessage);
                    alertRepository.save(newAlert);
                }
            }
        }
    }
}