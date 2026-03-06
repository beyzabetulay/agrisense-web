package io.agrisense.domain.model;

public class AlertRule {
    private Long id;
    private Long sensorId;
    private String ruleName;
    private ECondition condition;
    private double threshold;
    private boolean isActive;

    public AlertRule() {
    }

    public AlertRule(Long sensorId, String ruleName, ECondition condition, double threshold) {
        this.sensorId = sensorId;
        this.ruleName = ruleName;
        this.condition = condition;
        this.threshold = threshold;
        this.isActive = true;
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

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public ECondition getCondition() {
        return condition;
    }

    public void setCondition(ECondition condition) {
        this.condition = condition;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public boolean isViolated(double measurementValue) {
        switch (condition) {
            case GREATER_THAN:
                return measurementValue > threshold;
            case LESS_THAN:
                return measurementValue < threshold;
            case EQUAL:
                return Math.abs(measurementValue - threshold) < 0.001;
            default:
                return false;
        }
    }
}
