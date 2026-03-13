package io.agrisense.adapter.out.adapters;

import io.agrisense.adapter.out.persistence.entity.MeasurementEntity;
import io.agrisense.domain.model.Measurement;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeasurementPersistenceAdapterTest {

    private EntityManager entityManager;
    private MeasurementPersistenceAdapter adapter;

    @BeforeEach
    public void setup() {
        entityManager = Mockito.mock(EntityManager.class);
        adapter = new MeasurementPersistenceAdapter();
        adapter.entityManager = entityManager;
    }

    @Test
    public void testSave_WithValidMeasurement_Persists() {
        Measurement measurement = new Measurement(1L, 23.5, "C");

        adapter.save(measurement);

        verify(entityManager).persist(any(MeasurementEntity.class));
    }

    @Test
    public void testSave_SetsSensorReference_UsingGetReference() {
        Measurement measurement = new Measurement(1L, 23.5, "C");

        adapter.save(measurement);

        verify(entityManager).getReference(any(), any());
    }
}
