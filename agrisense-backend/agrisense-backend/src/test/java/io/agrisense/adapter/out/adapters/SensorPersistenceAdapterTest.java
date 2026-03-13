package io.agrisense.adapter.out.adapters;

import io.agrisense.adapter.out.persistence.entity.FieldEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.ESensorType;
import io.agrisense.domain.model.Sensor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SensorPersistenceAdapterTest {

    private EntityManager entityManager;
    private SensorPersistenceAdapter adapter;

    @BeforeEach
    public void setup() {
        entityManager = Mockito.mock(EntityManager.class);
        adapter = new SensorPersistenceAdapter();
        adapter.entityManager = entityManager;
    }

    @Test
    public void testFindById_WithValidId_ReturnsEntity() {
        SensorEntity entity = new SensorEntity();
        entity.setId(1L);
        entity.setName("Sensor1");

        when(entityManager.find(SensorEntity.class, 1L)).thenReturn(entity);

        Sensor result = adapter.findById(1L);

        assertNotNull(result);
        assertEquals("Sensor1", result.getName());
        verify(entityManager).find(SensorEntity.class, 1L);
    }

    @Test
    public void testFindById_WithInvalidId_ReturnsNull() {
        when(entityManager.find(SensorEntity.class, 999L)).thenReturn(null);

        Sensor result = adapter.findById(999L);

        assertNull(result);
    }

    @Test
    public void testSave_NewSensor_Persists() {
        Sensor sensor = new Sensor("Sensor1", ESensorType.TEMPERATURE, "key1", 1L);

        SensorEntity entity = new SensorEntity();
        entity.setId(null);
        entity.setName("Sensor1");

        when(entityManager.find(SensorEntity.class, null)).thenReturn(null);
        // Mock getReference to return FieldEntity, not SensorEntity
        FieldEntity fieldRef = new FieldEntity();
        when(entityManager.getReference(FieldEntity.class, 1L)).thenReturn(fieldRef);

        Sensor result = adapter.save(sensor);

        assertNotNull(result);
        verify(entityManager).persist(any(SensorEntity.class));
    }

    @Test
    public void testFindAll_ReturnsAllSensors() {
        SensorEntity s1 = new SensorEntity();
        s1.setId(1L);
        s1.setName("Sensor1");

        SensorEntity s2 = new SensorEntity();
        s2.setId(2L);
        s2.setName("Sensor2");

        TypedQuery<SensorEntity> query = Mockito.mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT s FROM SensorEntity s", SensorEntity.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(s1, s2));

        List<Sensor> result = adapter.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindAll_WithNoResults_ReturnsEmptyList() {
        TypedQuery<SensorEntity> query = Mockito.mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT s FROM SensorEntity s", SensorEntity.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Sensor> result = adapter.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
