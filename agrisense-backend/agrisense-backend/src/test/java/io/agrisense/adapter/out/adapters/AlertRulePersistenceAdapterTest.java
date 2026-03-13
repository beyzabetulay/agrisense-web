package io.agrisense.adapter.out.adapters;

import io.agrisense.adapter.out.persistence.entity.AlertRuleEntity;
import io.agrisense.domain.model.AlertRule;
import io.agrisense.domain.model.ECondition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AlertRulePersistenceAdapterTest {

    private EntityManager entityManager;
    private AlertRulePersistenceAdapter adapter;

    @BeforeEach
    public void setup() {
        entityManager = Mockito.mock(EntityManager.class);
        adapter = new AlertRulePersistenceAdapter();
        adapter.entityManager = entityManager;
    }

    @Test
    public void testSave_NewRule_Persists() {
        AlertRule rule = new AlertRule(1L, "HighTemp", ECondition.GREATER_THAN, 30.0);

        AlertRuleEntity entity = new AlertRuleEntity();
        entity.setRuleName("HighTemp");

        adapter.save(rule);

        verify(entityManager).persist(any(AlertRuleEntity.class));
    }

    @Test
    public void testSave_ExistingRule_Merges() {
        AlertRule rule = new AlertRule(1L, "HighTemp", ECondition.GREATER_THAN, 30.0);
        rule.setId(1L);

        adapter.save(rule);

        verify(entityManager).merge(any(AlertRuleEntity.class));
    }

    @Test
    public void testFindActiveBySensorId_ReturnsList() {
        AlertRuleEntity e1 = new AlertRuleEntity();
        e1.setId(1L);
        AlertRuleEntity e2 = new AlertRuleEntity();
        e2.setId(2L);

        TypedQuery<AlertRuleEntity> query = Mockito.mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(AlertRuleEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(e1, e2));

        List<AlertRule> result = adapter.findActiveBySensorId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindActiveBySensorId_WithNoResults_ReturnsEmptyList() {
        TypedQuery<AlertRuleEntity> query = Mockito.mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(AlertRuleEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<AlertRule> result = adapter.findActiveBySensorId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
