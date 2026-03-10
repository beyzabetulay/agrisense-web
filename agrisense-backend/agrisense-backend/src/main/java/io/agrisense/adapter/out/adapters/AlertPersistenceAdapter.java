package io.agrisense.adapter.out.adapters;

import java.util.List;

import io.agrisense.adapter.out.Mapper.AgriSenseMapper;
import io.agrisense.adapter.out.persistence.entity.AlertEntity;
import io.agrisense.adapter.out.persistence.entity.AlertRuleEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.Alert;
import io.agrisense.ports.out.IAlertRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AlertPersistenceAdapter implements IAlertRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Alert save(Alert alert) {
        AlertEntity entity = AgriSenseMapper.toEntity(alert);
        if (alert.getSensorId() != null) {
            entity.setSensor(entityManager.getReference(SensorEntity.class, alert.getSensorId()));
        }
        if (alert.getRuleId() != null) {
            entity.setRule(entityManager.getReference(AlertRuleEntity.class, alert.getRuleId()));
        }
        if (entity.getId() != null) {
            // Update existing alert (e.g. closing it)
            entity = entityManager.merge(entity);
        } else {
            // New alert
            entity.setCreatedAt(java.time.Instant.now());
            entityManager.persist(entity);
        }
        alert.setId(entity.getId());
        return alert;
    }

    @Override
    public Alert findOpenAlert(Long sensorId, Long ruleId) {
        TypedQuery<AlertEntity> q = entityManager.createQuery(
                "SELECT a FROM AlertEntity a WHERE a.sensor.id = :sensorId AND a.rule.id = :ruleId AND a.resolved = false",
                AlertEntity.class);
        q.setParameter("sensorId", sensorId);
        q.setParameter("ruleId", ruleId);
        List<AlertEntity> results = q.getResultList();
        if (results.isEmpty())
            return null;
        return AgriSenseMapper.toDomain(results.get(0));
    }

    @Override
    public List<Alert> findByStatus(io.agrisense.domain.model.EAlertStatus status, int offset, int limit) {
        String jpql = "SELECT a FROM AlertEntity a";
        boolean resolved = false;

        if (status != null) {
            resolved = (status == io.agrisense.domain.model.EAlertStatus.CLOSED);
            jpql += " WHERE a.resolved = :resolved";
        }

        jpql += " ORDER BY a.createdAt DESC";

        TypedQuery<AlertEntity> query = entityManager.createQuery(jpql, AlertEntity.class);

        if (status != null) {
            query.setParameter("resolved", resolved);
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultStream()
                .map(AgriSenseMapper::toDomain)
                .toList();
    }

    @Override
    public long countByStatus(io.agrisense.domain.model.EAlertStatus status) {
        String jpql = "SELECT COUNT(a) FROM AlertEntity a";
        boolean resolved = false;

        if (status != null) {
            resolved = (status == io.agrisense.domain.model.EAlertStatus.CLOSED);
            jpql += " WHERE a.resolved = :resolved";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);

        if (status != null) {
            query.setParameter("resolved", resolved);
        }

        return query.getSingleResult();
    }
}
