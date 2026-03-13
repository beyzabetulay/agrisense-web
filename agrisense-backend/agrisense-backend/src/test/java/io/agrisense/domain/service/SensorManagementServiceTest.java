package io.agrisense.domain.service;

import io.agrisense.domain.model.ESensorType;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.out.ISensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SensorManagementServiceTest {

    private ISensorRepository repository;
    private SensorManagementService service;

    @BeforeEach
    public void setup() {
        repository = Mockito.mock(ISensorRepository.class);
        service = new SensorManagementService(repository);
    }

    // --- CREATE SENSOR TESTS ---

    @Test
    public void testCreateSensor_SavesAndReturns() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        Sensor saved = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        saved.setId(5L);

        when(repository.save(any(Sensor.class))).thenReturn(saved);

        Sensor result = service.createSensor(sensor);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Sensor1", result.getName());
        Mockito.verify(repository).save(sensor);
    }

    @Test
    public void testCreateSensor_WithNullFields_StillPersists() {
        Sensor sensor = new Sensor(null, null, null, null);
        Sensor saved = new Sensor(null, null, null, null);
        saved.setId(1L);

        when(repository.save(any(Sensor.class))).thenReturn(saved);

        Sensor result = service.createSensor(sensor);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // --- GET ALL SENSORS TESTS ---

    @Test
    public void testGetAllSensors_ReturnsAll() {
        Sensor s1 = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        s1.setId(1L);
        Sensor s2 = new Sensor("Sensor2", ESensorType.MOISTURE, "key2", 2L);
        s2.setId(2L);

        List<Sensor> sensors = Arrays.asList(s1, s2);
        when(repository.findAll()).thenReturn(sensors);

        List<Sensor> result = service.getAllSensors();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Sensor1", result.get(0).getName());
        assertEquals("Sensor2", result.get(1).getName());
    }

    @Test
    public void testGetAllSensors_ReturnsEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Sensor> result = service.getAllSensors();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- GET SENSOR BY ID TESTS ---

    @Test
    public void testGetSensorById_WithValidId_ReturnsSensor() {
        Sensor s = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        s.setId(3L);

        when(repository.findById(3L)).thenReturn(s);

        Sensor result = service.getSensorById(3L);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Sensor1", result.getName());
    }

    @Test
    public void testGetSensorById_WithInvalidId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.getSensorById(999L);
        });
    }

    @Test
    public void testGetSensorById_WithNullRepository_ThrowsException() {
        when(repository.findById(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.getSensorById(1L);
        });
    }
}
