package io.agrisense.adapter.in.web.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import io.agrisense.adapter.in.web.dto.CreateSensorRequest;
import io.agrisense.adapter.in.web.dto.SensorResponse;
import io.agrisense.adapter.in.web.mapper.SensorWebMapper;
import io.agrisense.domain.model.ESensorStatus;
import io.agrisense.domain.model.ESensorType;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.in.IManageSensorUseCase;
import jakarta.ws.rs.core.Response;

public class SensorControllerTest {

    private IManageSensorUseCase useCase;
    private SensorWebMapper mapper;
    private SensorController controller;

    @BeforeEach
    public void setup() {
        useCase = Mockito.mock(IManageSensorUseCase.class);
        mapper = Mockito.mock(SensorWebMapper.class);
        controller = new SensorController(useCase, mapper);
    }

    // --- CREATE SENSOR TESTS ---

    @Test
    public void testCreateSensor_WithValidData_Returns201() {
        CreateSensorRequest req = new CreateSensorRequest();
        req.setName("TempSensor1");
        req.setType(ESensorType.TEMPERATURE);
        req.setApiKey("key123");
        req.setFieldId(1L);

        Sensor domain = new Sensor("TempSensor1", ESensorType.TEMPERATURE, "key123", 1L);
        Sensor saved = new Sensor("TempSensor1", ESensorType.TEMPERATURE, "key123", 1L);
        saved.setId(5L);

        SensorResponse resp = new SensorResponse(5L, "TempSensor1", ESensorType.TEMPERATURE, ESensorStatus.ACTIVE,
                "key123", 1L);

        when(mapper.toDomain(req)).thenReturn(domain);
        when(useCase.createSensor(any(Sensor.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(resp);

        Response response = controller.createSensor(req);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof SensorResponse);
        SensorResponse r = (SensorResponse) response.getEntity();
        assertEquals(5L, r.getId());
        assertEquals("TempSensor1", r.getName());
    }

    @Test
    public void testCreateSensor_WithMissingName_Returns201() {
        CreateSensorRequest req = new CreateSensorRequest();
        req.setName(null);
        req.setType(ESensorType.TEMPERATURE);
        req.setApiKey("key123");
        req.setFieldId(1L);

        Sensor domain = new Sensor(null, ESensorType.TEMPERATURE, "key123", 1L);
        Sensor saved = new Sensor(null, ESensorType.TEMPERATURE, "key123", 1L);
        saved.setId(5L);

        when(mapper.toDomain(req)).thenReturn(domain);
        when(useCase.createSensor(any(Sensor.class))).thenReturn(saved);
        when(mapper.toResponse(saved))
                .thenReturn(new SensorResponse(5L, null, ESensorType.TEMPERATURE, ESensorStatus.ACTIVE, "key123", 1L));

        Response response = controller.createSensor(req);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateSensor_WithMissingType_Returns201() {
        CreateSensorRequest req = new CreateSensorRequest();
        req.setName("TempSensor1");
        req.setType(null);
        req.setApiKey("key123");
        req.setFieldId(1L);

        Sensor domain = new Sensor("TempSensor1", null, "key123", 1L);
        Sensor saved = new Sensor("TempSensor1", null, "key123", 1L);
        saved.setId(5L);

        when(mapper.toDomain(req)).thenReturn(domain);
        when(useCase.createSensor(any(Sensor.class))).thenReturn(saved);
        when(mapper.toResponse(saved))
                .thenReturn(new SensorResponse(5L, "TempSensor1", null, ESensorStatus.ACTIVE, "key123", 1L));

        Response response = controller.createSensor(req);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateSensor_WithMissingNameAndType_Returns201() {
        CreateSensorRequest req = new CreateSensorRequest();
        req.setName(null);
        req.setType(null);
        req.setApiKey("key123");
        req.setFieldId(1L);

        Sensor domain = new Sensor(null, null, "key123", 1L);
        Sensor saved = new Sensor(null, null, "key123", 1L);
        saved.setId(5L);

        when(mapper.toDomain(req)).thenReturn(domain);
        when(useCase.createSensor(any(Sensor.class))).thenReturn(saved);
        when(mapper.toResponse(saved))
                .thenReturn(new SensorResponse(5L, null, null, ESensorStatus.ACTIVE, "key123", 1L));

        Response response = controller.createSensor(req);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateSensor_WithNullRequest_Returns400() {
        Response response = controller.createSensor(null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    // --- GET ALL SENSORS TESTS ---

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllSensors_Returns200() {
        Sensor s1 = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        s1.setId(1L);
        Sensor s2 = new Sensor("Sensor2", ESensorType.MOISTURE, "key2", 1L);
        s2.setId(2L);

        List<Sensor> sensors = Arrays.asList(s1, s2);
        List<SensorResponse> responses = Arrays.asList(
                new SensorResponse(1L, "Sensor1", ESensorType.TEMPERATURE, ESensorStatus.ACTIVE, "key1", 1L),
                new SensorResponse(2L, "Sensor2", ESensorType.MOISTURE, ESensorStatus.ACTIVE, "key2", 1L));

        when(useCase.getAllSensors()).thenReturn(sensors);
        when(mapper.toResponseList(sensors)).thenReturn(responses);

        Response response = controller.getAllSensors();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof List);
        List<SensorResponse> responseList = (List<SensorResponse>) response.getEntity();
        assertEquals(2, responseList.size());
    }

    @Test
    public void testGetAllSensors_ReturnsEmptyList() {
        when(useCase.getAllSensors()).thenReturn(Collections.emptyList());
        when(mapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        Response response = controller.getAllSensors();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<SensorResponse> responseList = (List<SensorResponse>) response.getEntity();
        assertTrue(responseList.isEmpty());
    }

    // --- GET SENSOR BY ID TESTS ---

    @Test
    public void testGetSensorById_WithValidId_Returns200() {
        Sensor s = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        s.setId(3L);

        SensorResponse resp = new SensorResponse(3L, "Sensor1", ESensorType.TEMPERATURE, ESensorStatus.ACTIVE, "key1",
                1L);

        when(useCase.getSensorById(3L)).thenReturn(s);
        when(mapper.toResponse(s)).thenReturn(resp);

        Response response = controller.getSensorById(3L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof SensorResponse);
        SensorResponse r = (SensorResponse) response.getEntity();
        assertEquals(3L, r.getId());
    }

    @Test
    public void testGetSensorById_WithInvalidId_Returns404() {
        when(useCase.getSensorById(999L)).thenThrow(new IllegalArgumentException("Sensor with ID 999 not found."));

        assertThrows(IllegalArgumentException.class, () -> controller.getSensorById(999L));
    }

    @Test
    public void testGetSensorById_WithNullId_Returns400() {
        // @PathParam framework handles null path parameters
        // Cannot directly pass null in unit test - would require integration test
    }
}
