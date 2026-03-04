package io.agrisense.adapter.out.adapters;

import io.agrisense.adapter.out.persistence.entity.AlertEntity;
import io.agrisense.domain.model.Alert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AlertPersistenceAdapterTest {

    private EntityManager entityManager;
    private AlertPersistenceAdapter adapter;

    @BeforeEach
    public void setup() {
        entityManager = Mockito.mock(EntityManager.class);
        adapter = new AlertPersistenceAdapter();
        adapter.entityManager = entityManager;
    }

    @Test
    public void testSave_WithValidAlert_Persists() {
        Alert alert = new Alert(1L, 1L, "Test alert");

        adapter.save(alert);

        verify(entityManager).persist(any(AlertEntity.class));
    }

    @Test
    public void testFindOpenAlert_WithExistingAlert_Returns() {
        AlertEntity entity = new AlertEntity();
        entity.setId(1L);

        TypedQuery<AlertEntity> query = Mockito.mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(AlertEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.Collections.singletonList(entity));

        Alert result = adapter.findOpenAlert(1L, 1L);

        assertNotNull(result);
    }

    @Test
    public void testFindOpenAlert_WithNoOpenAlert_ReturnsNull() {
        TypedQuery<AlertEntity> query = Mockito.mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(AlertEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        Alert result = adapter.findOpenAlert(1L, 1L);

        assertNull(result);
    }
}
