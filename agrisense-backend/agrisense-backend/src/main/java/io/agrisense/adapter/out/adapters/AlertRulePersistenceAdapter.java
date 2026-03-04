package io.agrisense.adapter.out.adapters;

import java.util.List;
import java.util.stream.Collectors;

import io.agrisense.adapter.out.Mapper.AgriSenseMapper;
import io.agrisense.adapter.out.persistence.entity.AlertRuleEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.AlertRule;
import io.agrisense.ports.out.IAlertRuleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AlertRulePersistenceAdapter implements IAlertRuleRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public AlertRule save(AlertRule alertRule) {
        if (alertRule == null) {
            return null;
        }
        AlertRuleEntity entity = AgriSenseMapper.toEntity(alertRule);
        if (alertRule.getSensorId() != null) {
            SensorEntity sensor = entityManager.getReference(SensorEntity.class, alertRule.getSensorId());
            entity.setSensor(sensor);
        }
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return AgriSenseMapper.toDomain(entity);
    }

    @Override
    public List<AlertRule> findActiveBySensorId(Long sensorId) {
        TypedQuery<AlertRuleEntity> q = entityManager.createQuery(
            "SELECT a FROM AlertRuleEntity a WHERE a.sensor.id = :sensorId AND a.active = true",
            AlertRuleEntity.class
        );
        q.setParameter("sensorId", sensorId);
        List<AlertRuleEntity> list = q.getResultList();
        return list.stream().map(AgriSenseMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public AlertRule findById(Long id) {
        AlertRuleEntity entity = entityManager.find(AlertRuleEntity.class, id);
        return AgriSenseMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void delete(AlertRule alertRule) {
        if (alertRule != null && alertRule.getId() != null) {
            AlertRuleEntity entity = entityManager.find(AlertRuleEntity.class, alertRule.getId());
            if (entity != null) {
                entityManager.remove(entity);
            }
        }
    }
}
