package io.agrisense.adapter.out.Mapper;

import io.agrisense.adapter.out.persistence.entity.AlertEntity;
import io.agrisense.adapter.out.persistence.entity.AlertRuleEntity;
import io.agrisense.adapter.out.persistence.entity.FarmerEntity;
import io.agrisense.adapter.out.persistence.entity.FieldEntity;
import io.agrisense.adapter.out.persistence.entity.MeasurementEntity;
import io.agrisense.adapter.out.persistence.entity.SensorEntity;
import io.agrisense.domain.model.Alert;
import io.agrisense.domain.model.AlertRule;
import io.agrisense.domain.model.Farmer;
import io.agrisense.domain.model.Field;
import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.Sensor;

public final class AgriSenseMapper {

    private AgriSenseMapper() {
    }

    public static Sensor toDomain(SensorEntity e) {
        if (e == null)
            return null;
        Sensor s = new Sensor();
        s.setId(e.getId());
        s.setName(e.getName());
        s.setApiKey(e.getApiKey());
        s.setType(e.getType());
        s.setStatus(e.getStatus());
        if (e.getField() != null)
            s.setFieldId(e.getField().getId());
        return s;
    }

    public static SensorEntity toEntity(Sensor s) {
        if (s == null)
            return null;
        SensorEntity e = new SensorEntity();
        e.setId(s.getId());
        e.setName(s.getName());
        e.setApiKey(s.getApiKey());
        e.setType(s.getType());
        e.setStatus(s.getStatus());
        return e;
    }

    public static Field toDomain(FieldEntity e) {
        if (e == null)
            return null;
        Field f = new Field();
        f.setId(e.getId());
        f.setName(e.getName());
        f.setLocation(e.getLocation());
        if (e.getFarmer() != null)
            f.setFarmerId(e.getFarmer().getId());
        return f;
    }

    public static FieldEntity toEntity(Field f) {
        if (f == null)
            return null;
        FieldEntity e = new FieldEntity();
        e.setId(f.getId());
        e.setName(f.getName());
        e.setLocation(f.getLocation());
        return e;
    }

    public static Farmer toDomain(FarmerEntity e) {
        if (e == null)
            return null;
        Farmer f = new Farmer();
        f.setId(e.getId());
        f.setUsername(e.getUsername());
        f.setEmail(e.getEmail());
        return f;
    }

    public static FarmerEntity toEntity(Farmer f) {
        if (f == null)
            return null;
        FarmerEntity e = new FarmerEntity();
        e.setId(f.getId());
        e.setUsername(f.getUsername());
        e.setEmail(f.getEmail());
        return e;
    }

    public static AlertRule toDomain(AlertRuleEntity e) {
        if (e == null)
            return null;
        AlertRule r = new AlertRule();
        r.setId(e.getId());
        if (e.getSensor() != null)
            r.setSensorId(e.getSensor().getId());
        r.setRuleName(e.getRuleName());
        r.setCondition(e.getCondition());
        r.setThreshold(e.getThreshold());
        r.setActive(e.isActive());
        return r;
    }

    public static AlertRuleEntity toEntity(AlertRule r) {
        if (r == null)
            return null;
        AlertRuleEntity e = new AlertRuleEntity();
        e.setId(r.getId());
        e.setRuleName(r.getRuleName());
        e.setCondition(r.getCondition());
        e.setThreshold(r.getThreshold());
        e.setActive(r.isActive());
        return e;
    }

    public static Measurement toDomain(MeasurementEntity e) {
        if (e == null)
            return null;
        Measurement m = new Measurement();
        m.setId(e.getId());
        if (e.getSensor() != null)
            m.setSensorId(e.getSensor().getId());
        m.setValue(e.getValue());
        m.setUnit(e.getUnit());
        m.setTimestamp(e.getCreatedAt());
        return m;
    }

    public static MeasurementEntity toEntity(Measurement m) {
        if (m == null)
            return null;
        MeasurementEntity e = new MeasurementEntity();
        e.setId(m.getId());
        e.setValue(m.getValue());
        e.setUnit(m.getUnit());
        return e;
    }

    public static Alert toDomain(AlertEntity e) {
        if (e == null)
            return null;
        Alert a = new Alert();
        a.setId(e.getId());
        if (e.getSensor() != null)
            a.setSensorId(e.getSensor().getId());
        if (e.getRule() != null)
            a.setRuleId(e.getRule().getId());
        a.setMessage(e.getMessage());
        a.setStatus(e.isResolved() ? io.agrisense.domain.model.EAlertStatus.CLOSED
                : io.agrisense.domain.model.EAlertStatus.OPEN);
        if (e.getCreatedAt() != null)
            a.setCreatedAt(e.getCreatedAt());
        if (e.isResolved() && e.getCreatedAt() != null)
            a.setClosedAt(e.getCreatedAt());
        return a;
    }

    public static AlertEntity toEntity(Alert a) {
        if (a == null)
            return null;
        AlertEntity e = new AlertEntity();
        e.setId(a.getId());
        e.setMessage(a.getMessage());
        e.setResolved(a.getStatus() == io.agrisense.domain.model.EAlertStatus.CLOSED);
        if (a.getCreatedAt() != null)
            e.setCreatedAt(a.getCreatedAt());
        if (a.getClosedAt() != null)
            e.setCreatedAt(a.getClosedAt());
        return e;
    }
}
