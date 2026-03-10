package io.agrisense.adapter.out.adapters;

import java.util.List;
import java.util.stream.Collectors;

import io.agrisense.adapter.out.Mapper.AgriSenseMapper;
import io.agrisense.adapter.out.persistence.entity.FieldEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.out.ISensorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SensorPersistenceAdapter implements ISensorRepository {

    @Inject
    EntityManager entityManager;

    @Override
    public Sensor findById(Long id) {
        SensorEntity entity = entityManager.find(SensorEntity.class, id);
        return AgriSenseMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Sensor save(Sensor sensor) {
        if (sensor.getId() == null) {
            SensorEntity entity = AgriSenseMapper.toEntity(sensor);
            if (sensor.getFieldId() != null) {
                FieldEntity ref = entityManager.getReference(FieldEntity.class, sensor.getFieldId());
                entity.setField(ref);
            }
            entityManager.persist(entity);
            sensor.setId(entity.getId());
            return sensor;
        } else {
            SensorEntity existing = entityManager.find(SensorEntity.class, sensor.getId());
            existing.setName(sensor.getName());
            existing.setApiKey(sensor.getApiKey());
            existing.setType(sensor.getType());
            existing.setStatus(sensor.getStatus());
            if (sensor.getFieldId() != null) {
                existing.setField(entityManager.getReference(FieldEntity.class, sensor.getFieldId()));
            } else {
                existing.setField(null);
            }
            entityManager.merge(existing);
            return AgriSenseMapper.toDomain(existing);
        }
    }

    @Override
    public List<Sensor> findAll() {
        TypedQuery<SensorEntity> q = entityManager.createQuery("SELECT s FROM SensorEntity s", SensorEntity.class);
        List<SensorEntity> entities = q.getResultList();
        return entities.stream().map(AgriSenseMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Sensor sensor) {
        if (sensor != null && sensor.getId() != null) {
            SensorEntity entity = entityManager.find(SensorEntity.class, sensor.getId());
            if (entity != null) {
                entityManager.remove(entity);
            }
        }
    }
}
