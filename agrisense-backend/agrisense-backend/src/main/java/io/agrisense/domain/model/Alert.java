package io.agrisense.domain.model;

import java.time.Instant;

public class Alert {
    private Long id;
    private Long sensorId;
    private Long ruleId;
    private String message;
    private EAlertStatus status;
    private Instant createdAt;
    private Instant closedAt;

    public Alert() {
    }

    public Alert(Long sensorId, Long ruleId, String message) {
        this.sensorId = sensorId;
        this.ruleId = ruleId;
        this.message = message;
        this.status = EAlertStatus.OPEN;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EAlertStatus getStatus() {
        return status;
    }

    public void setStatus(EAlertStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public boolean isOpen() {
        return status == EAlertStatus.OPEN;
    }

    public void close() {
        this.status = EAlertStatus.CLOSED;
        this.closedAt = Instant.now();
    }
}
