package io.agrisense.adapter.out.adapters;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.agrisense.adapter.out.Mapper.AgriSenseMapper;
import io.agrisense.adapter.out.persistence.entity.MeasurementEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.PagedResult;
import io.agrisense.ports.out.IMeasurementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MeasurementPersistenceAdapter implements IMeasurementRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Measurement save(Measurement measurement) {
        MeasurementEntity entity = AgriSenseMapper.toEntity(measurement);
        if (measurement.getSensorId() != null) {
            entity.setSensor(entityManager.getReference(SensorEntity.class, measurement.getSensorId()));
        }
        entity.setCreatedAt(java.time.Instant.now());
        entityManager.persist(entity);
        measurement.setId(entity.getId());
        return measurement;
    }

    @Override
    public PagedResult<Measurement> findByFilters(Long fieldId, Long sensorId, Instant from, Instant to, int page,
            int size) {
        StringBuilder jpql = new StringBuilder("SELECT m FROM MeasurementEntity m WHERE 1=1");

        if (sensorId != null) {
            jpql.append(" AND m.sensor.id = :sensorId");
        }
        if (fieldId != null) {
            jpql.append(" AND m.sensor.field.id = :fieldId");
        }
        if (from != null) {
            jpql.append(" AND m.createdAt >= :from");
        }
        if (to != null) {
            jpql.append(" AND m.createdAt <= :to");
        }
        jpql.append(" ORDER BY m.createdAt DESC");

        TypedQuery<MeasurementEntity> query = entityManager.createQuery(jpql.toString(), MeasurementEntity.class);
        if (sensorId != null) {
            query.setParameter("sensorId", sensorId);
        }
        if (fieldId != null) {
            query.setParameter("fieldId", fieldId);
        }
        if (from != null) {
            query.setParameter("from", from);
        }
        if (to != null) {
            query.setParameter("to", to);
        }
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<MeasurementEntity> entities = query.getResultList();
        List<Measurement> measurements = entities.stream()
                .map(AgriSenseMapper::toDomain)
                .collect(Collectors.toList());

        long totalElements = countByFilters(fieldId, sensorId, from, to);

        return new PagedResult<>(measurements, page, size, totalElements);
    }

    @Override
    public long countByFilters(Long fieldId, Long sensorId, Instant from, Instant to) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(m) FROM MeasurementEntity m WHERE 1=1");

        if (sensorId != null) {
            jpql.append(" AND m.sensor.id = :sensorId");
        }
        if (fieldId != null) {
            jpql.append(" AND m.sensor.field.id = :fieldId");
        }
        if (from != null) {
            jpql.append(" AND m.createdAt >= :from");
        }
        if (to != null) {
            jpql.append(" AND m.createdAt <= :to");
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        if (sensorId != null) {
            query.setParameter("sensorId", sensorId);
        }
        if (fieldId != null) {
            query.setParameter("fieldId", fieldId);
        }
        if (from != null) {
            query.setParameter("from", from);
        }
        if (to != null) {
            query.setParameter("to", to);
        }

        return query.getSingleResult();
    }
}
