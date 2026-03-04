package io.agrisense.adapter.in.web.dto;

import java.time.Instant;

public class AlertResponse {
    private Long id;
    private Long sensorId;
    private Long ruleId;
    private String message;
    private String status;
    private Instant createdAt;
    private Instant closedAt;
    private HateoasLinks _links;

    public AlertResponse() {
    }

    public AlertResponse(Long id, Long sensorId, Long ruleId, String message, 
                        String status, Instant createdAt, Instant closedAt) {
        this.id = id;
        this.sensorId = sensorId;
        this.ruleId = ruleId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public HateoasLinks get_links() {
        return _links;
    }

    public void set_links(HateoasLinks _links) {
        this._links = _links;
    }
}
