package io.agrisense.domain.service;

import io.agrisense.domain.model.AlertRule;
import io.agrisense.domain.model.ECondition;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.out.IAlertRuleRepository;
import io.agrisense.ports.out.ISensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertRuleServiceTest {

    private ISensorRepository sensorRepository;
    private IAlertRuleRepository alertRuleRepository;
    private AlertRuleService service;

    @BeforeEach
    public void setup() {
        sensorRepository = Mockito.mock(ISensorRepository.class);
        alertRuleRepository = Mockito.mock(IAlertRuleRepository.class);
        service = new AlertRuleService(sensorRepository, alertRuleRepository);
    }

    // --- CREATE RULE TESTS ---

    @Test
    public void testCreateRule_WithValidData_SavesAndReturns() {
        Sensor sensor = new Sensor("Sensor1", null, "key", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule = new AlertRule(1L, "HighTemp", ECondition.GREATER_THAN, 30.0);
        AlertRule saved = new AlertRule(1L, "HighTemp", ECondition.GREATER_THAN, 30.0);
        saved.setId(5L);

        when(alertRuleRepository.save(any(AlertRule.class))).thenReturn(saved);

        AlertRule result = service.createRule(1L, rule);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(alertRuleRepository).save(any(AlertRule.class));
    }

    @Test
    public void testCreateRule_WithNullSensorId_ThrowsException() {
        AlertRule rule = new AlertRule();

        assertThrows(IllegalArgumentException.class, () -> {
            service.createRule(null, rule);
        });

        verify(alertRuleRepository, never()).save(any());
    }

    @Test
    public void testCreateRule_WithNullRule_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createRule(1L, null);
        });

        verify(alertRuleRepository, never()).save(any());
    }

    @Test
    public void testCreateRule_WithInvalidSensorId_ThrowsException() {
        when(sensorRepository.findById(999L)).thenReturn(null);

        AlertRule rule = new AlertRule();

        assertThrows(IllegalArgumentException.class, () -> {
            service.createRule(999L, rule);
        });

        verify(alertRuleRepository, never()).save(any());
    }

    @Test
    public void testCreateRule_WithSensorHasNoField_ThrowsException() {
        Sensor sensor = new Sensor("Sensor1", null, "key", null);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);

        AlertRule rule = new AlertRule();

        assertThrows(IllegalArgumentException.class, () -> {
            service.createRule(1L, rule);
        });

        verify(alertRuleRepository, never()).save(any());
    }

    // --- GET ACTIVE RULES TESTS ---

    @Test
    public void testGetActiveRules_WithValidSensorId_ReturnsRules() {
        AlertRule r1 = new AlertRule();
        r1.setId(1L);
        AlertRule r2 = new AlertRule();
        r2.setId(2L);

        Sensor sensor = new Sensor("Sensor1", null, "key", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Arrays.asList(r1, r2));

        List<AlertRule> result = service.getActiveRules(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetActiveRules_WithInvalidSensorId_ThrowsException() {
        when(sensorRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.getActiveRules(999L);
        });

        verify(alertRuleRepository, never()).findActiveBySensorId(anyLong());
    }

    @Test
    public void testGetActiveRules_WithNullSensorId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.getActiveRules(null);
        });

        verify(alertRuleRepository, never()).findActiveBySensorId(anyLong());
    }

    @Test
    public void testGetActiveRules_WithNoRules_ReturnsEmptyList() {
        Sensor sensor = new Sensor("Sensor1", null, "key", 1L);
        sensor.setId(1L);
        when(sensorRepository.findById(1L)).thenReturn(sensor);
        when(alertRuleRepository.findActiveBySensorId(1L)).thenReturn(Collections.emptyList());

        List<AlertRule> result = service.getActiveRules(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
