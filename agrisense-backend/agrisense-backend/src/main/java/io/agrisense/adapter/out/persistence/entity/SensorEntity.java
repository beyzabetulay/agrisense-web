package io.agrisense.adapter.out.persistence.entity;

import io.agrisense.domain.model.ESensorType;
import io.agrisense.domain.model.ESensorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensors")
public class SensorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "api_key")
    private String apiKey;

    @Enumerated(EnumType.STRING)
    private ESensorType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ESensorStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private FieldEntity field;

    public SensorEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ESensorType getType() {
        return type;
    }

    public void setType(ESensorType type) {
        this.type = type;
    }

    public ESensorStatus getStatus() {
        return status;
    }

    public void setStatus(ESensorStatus status) {
        this.status = status;
    }

    public FieldEntity getField() {
        return field;
    }

    public void setField(FieldEntity field) {
        this.field = field;
    }
}
