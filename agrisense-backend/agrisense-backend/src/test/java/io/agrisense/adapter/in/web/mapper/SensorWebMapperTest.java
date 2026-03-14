package io.agrisense.adapter.in.web.mapper;

import io.agrisense.adapter.in.web.dto.CreateSensorRequest;
import io.agrisense.adapter.in.web.dto.SensorResponse;
import io.agrisense.domain.model.ESensorType;
import io.agrisense.domain.model.Sensor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SensorWebMapperTest {

    private SensorWebMapper mapper = new SensorWebMapper();

    @Test
    public void testToDomain_FromValidRequest_MapsAllFields() {
        CreateSensorRequest req = new CreateSensorRequest();
        req.setName("TestSensor");
        req.setType(ESensorType.TEMPERATURE);
        req.setApiKey("key123");
        req.setFieldId(1L);

        Sensor domain = mapper.toDomain(req);

        assertNotNull(domain);
        assertEquals("TestSensor", domain.getName());
        assertEquals(ESensorType.TEMPERATURE, domain.getType());
        assertEquals("key123", domain.getApiKey());
        assertEquals(1L, domain.getFieldId());
    }

    @Test
    public void testToResponse_FromValidDomain_MapsAllFields() {
        Sensor sensor = new Sensor("TestSensor", ESensorType.TEMPERATURE, "key123", 1L);
        sensor.setId(5L);

        SensorResponse resp = mapper.toResponse(sensor);

        assertNotNull(resp);
        assertEquals(5L, resp.getId());
        assertEquals("TestSensor", resp.getName());
        assertEquals(ESensorType.TEMPERATURE, resp.getType());
        assertEquals("key123", resp.getApiKey());
        assertEquals(1L, resp.getFieldId());
    }

    @Test
    public void testToResponseList_ConvertsList() {
        Sensor s1 = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);
        s1.setId(1L);
        Sensor s2 = new Sensor("Sensor2", ESensorType.MOISTURE, "key2", 2L);
        s2.setId(2L);

        List<Sensor> sensors = Arrays.asList(s1, s2);
        List<SensorResponse> responses = mapper.toResponseList(sensors);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Sensor1", responses.get(0).getName());
        assertEquals("Sensor2", responses.get(1).getName());
    }

    @Test
    public void testToResponseList_EmptyList() {
        List<SensorResponse> responses = mapper.toResponseList(Collections.emptyList());

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testToDomain_WithNullInput_ReturnsNull() {
        Sensor domain = mapper.toDomain(null);

        assertNull(domain);
    }

    @Test
    public void testToResponse_WithNullInput_ReturnsNull() {
        SensorResponse resp = mapper.toResponse(null);

        assertNull(resp);
    }
}
