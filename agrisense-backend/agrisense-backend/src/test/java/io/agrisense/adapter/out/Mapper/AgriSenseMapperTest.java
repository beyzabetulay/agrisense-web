package io.agrisense.adapter.out.Mapper;

import io.agrisense.adapter.out.persistence.entity.AlertRuleEntity;
import io.agrisense.adapter.out.persistence.entity.AlertEntity;
import io.agrisense.adapter.out.persistence.entity.MeasurementEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class AgriSenseMapperTest {

    @Test
    public void testSensorToDomain_MapsAllFields() {
        SensorEntity entity = new SensorEntity();
        entity.setId(1L);
        entity.setName("Sensor1");
        entity.setApiKey("key1");
        entity.setType(ESensorType.TEMPERATURE);

        Sensor domain = AgriSenseMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("Sensor1", domain.getName());
        assertEquals("key1", domain.getApiKey());
        assertEquals(ESensorType.TEMPERATURE, domain.getType());
    }

    @Test
    public void testSensorToEntity_MapsAllFields() {
        Sensor domain = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        domain.setId(1L);

        SensorEntity entity = AgriSenseMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Sensor1", entity.getName());
        assertEquals("key1", entity.getApiKey());
        assertEquals(ESensorType.TEMPERATURE, entity.getType());
    }

    @Test
    public void testAlertRuleToDomain_MapsAllFields() {
        AlertRuleEntity entity = new AlertRuleEntity();
        entity.setId(1L);
        entity.setRuleName("HighTemp");
        entity.setCondition(ECondition.GREATER_THAN);
        entity.setThreshold(30.0);
        entity.setActive(true);

        AlertRule domain = AgriSenseMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("HighTemp", domain.getRuleName());
        assertEquals(ECondition.GREATER_THAN, domain.getCondition());
        assertEquals(30.0, domain.getThreshold());
        assertTrue(domain.isActive());
    }

    @Test
    public void testAlertRuleToEntity_MapsAllFields() {
        AlertRule domain = new AlertRule(1L, "HighTemp", ECondition.GREATER_THAN, 30.0);
        domain.setId(1L);

        AlertRuleEntity entity = AgriSenseMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("HighTemp", entity.getRuleName());
        assertEquals(ECondition.GREATER_THAN, entity.getCondition());
        assertEquals(30.0, entity.getThreshold());
    }

    @Test
    public void testMeasurementToDomain_MapsAllFields() {
        MeasurementEntity entity = new MeasurementEntity();
        entity.setId(1L);
        entity.setValue(23.5);
        entity.setUnit("C");

        Measurement domain = AgriSenseMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals(23.5, domain.getValue());
        assertEquals("C", domain.getUnit());
    }

    @Test
    public void testMeasurementToEntity_MapsAllFields() {
        Measurement domain = new Measurement(1L, 23.5, "C");
        domain.setId(1L);

        MeasurementEntity entity = AgriSenseMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(23.5, entity.getValue());
        assertEquals("C", entity.getUnit());
    }

    @Test
    public void testAlertToDomain_MapsStatusCorrectly() {
        AlertEntity entity = new AlertEntity();
        entity.setId(1L);
        entity.setMessage("Test alert");
        entity.setResolved(false);
        entity.setCreatedAt(Instant.now());

        Alert domain = AgriSenseMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("Test alert", domain.getMessage());
        assertEquals(EAlertStatus.OPEN, domain.getStatus());
    }

    @Test
    public void testAlertToEntity_MapsStatusCorrectly() {
        Alert domain = new Alert(1L, 1L, "Test alert");
        domain.setId(1L);
        domain.setStatus(EAlertStatus.OPEN);

        AlertEntity entity = AgriSenseMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Test alert", entity.getMessage());
        assertFalse(entity.isResolved());
    }

    @Test
    public void testToDomain_WithNullSensor_ReturnsNull() {
        Sensor domain = AgriSenseMapper.toDomain((SensorEntity) null);
        assertNull(domain);
    }

    @Test
    public void testToEntity_WithNullSensor_ReturnsNull() {
        SensorEntity entity = AgriSenseMapper.toEntity((Sensor) null);
        assertNull(entity);
    }

    @Test
    public void testToDomain_WithNullAlertRule_ReturnsNull() {
        AlertRule domain = AgriSenseMapper.toDomain((AlertRuleEntity) null);
        assertNull(domain);
    }

    @Test
    public void testToDomain_WithNullMeasurement_ReturnsNull() {
        Measurement domain = AgriSenseMapper.toDomain((MeasurementEntity) null);
        assertNull(domain);
    }

    @Test
    public void testToDomain_WithNullAlert_ReturnsNull() {
        Alert domain = AgriSenseMapper.toDomain((AlertEntity) null);
        assertNull(domain);
    }
}
