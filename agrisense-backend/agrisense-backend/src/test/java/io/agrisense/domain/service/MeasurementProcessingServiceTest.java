package io.agrisense.domain.service;

import io.agrisense.domain.model.*;
import io.agrisense.ports.out.IAlertRepository;
import io.agrisense.ports.out.IAlertRuleRepository;
import io.agrisense.ports.out.IMeasurementRepository;
import io.agrisense.ports.out.ISensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MeasurementProcessingServiceTest {

    private ISensorRepository sensorRepository;
    private IMeasurementRepository measurementRepository;
    private IAlertRuleRepository alertRuleRepository;
    private IAlertRepository alertRepository;
    private MeasurementProcessingService service;

    @BeforeEach
    public void setup() {
        sensorRepository = Mockito.mock(ISensorRepository.class);
        measurementRepository = Mockito.mock(IMeasurementRepository.class);
        alertRuleRepository = Mockito.mock(IAlertRuleRepository.class);
        alertRepository = Mockito.mock(IAlertRepository.class);
        service = new MeasurementProcessingService(sensorRepository, measurementRepository, alertRuleRepository, alertRepository);
    }

    @Test
    public void testProcessMeasurement_WithValidData_SavesAndChecksRules() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule = new AlertRule();
        rule.setId(1L);
        rule.setCondition(ECondition.GREATER_THAN);
        rule.setThreshold(25.0);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Collections.singletonList(rule));
        when(alertRepository.findOpenAlert(anyLong(), anyLong())).thenReturn(null);

        service.processMeasurement(1L, 30.0, "C");

        verify(measurementRepository).save(any(Measurement.class));
        verify(alertRuleRepository).findActiveBySensorId(1L);
    }

    @Test
    public void testProcessMeasurement_WithInvalidSensorId_ThrowsException() {
        when(sensorRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.processMeasurement(999L, 23.5, "C");
        });

        verify(measurementRepository, never()).save(any());
    }

    @Test
    public void testProcessMeasurement_WithRuleViolation_CreatesAlert() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule = new AlertRule();
        rule.setId(1L);
        rule.setRuleName("HighTemp");
        rule.setCondition(ECondition.GREATER_THAN);
        rule.setThreshold(25.0);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Collections.singletonList(rule));
        when(alertRepository.findOpenAlert(1L, 1L)).thenReturn(null);

        service.processMeasurement(1L, 30.0, "C");

        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    public void testProcessMeasurement_WithExistingOpenAlert_DoesNotCreateDuplicate() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule = new AlertRule();
        rule.setId(1L);
        rule.setCondition(ECondition.GREATER_THAN);
        rule.setThreshold(25.0);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Collections.singletonList(rule));

        Alert openAlert = new Alert(1L, 1L, "Already open");
        when(alertRepository.findOpenAlert(1L, 1L)).thenReturn(openAlert);

        service.processMeasurement(1L, 30.0, "C");

        verify(alertRepository, never()).save(any());
    }

    @Test
    public void testProcessMeasurement_WithNoActiveRules_OnlySavesMeasurement() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Collections.emptyList());

        service.processMeasurement(1L, 23.5, "C");

        verify(measurementRepository).save(any(Measurement.class));
        verify(alertRepository, never()).save(any());
    }

    @Test
    public void testProcessMeasurement_WithRuleNotViolated_NoAlert() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule = new AlertRule();
        rule.setId(1L);
        rule.setCondition(ECondition.GREATER_THAN);
        rule.setThreshold(25.0);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Collections.singletonList(rule));

        service.processMeasurement(1L, 20.0, "C");

        verify(alertRepository, never()).save(any());
    }

    @Test
    public void testProcessMeasurement_WithMultipleRules_EvaluatesAll() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule1 = new AlertRule();
        rule1.setId(1L);
        rule1.setCondition(ECondition.GREATER_THAN);
        rule1.setThreshold(25.0);

        AlertRule rule2 = new AlertRule();
        rule2.setId(2L);
        rule2.setCondition(ECondition.LESS_THAN);
        rule2.setThreshold(10.0);

        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Arrays.asList(rule1, rule2));
        when(alertRepository.findOpenAlert(anyLong(), anyLong())).thenReturn(null);

        service.processMeasurement(1L, 30.0, "C");

        verify(alertRepository, times(1)).save(any());
    }
}
