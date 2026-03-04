package io.agrisense.domain.model;

public class Sensor {
    private Long id;
    private String name;
    private ESensorType type;
    private ESensorStatus status;
    private String apiKey;
    private Long fieldId;

    public Sensor() {
    }

    public Sensor(String name, ESensorType type, String apiKey, Long fieldId) {
        this.name = name;
        this.type = type;
        this.apiKey = apiKey;
        this.fieldId = fieldId;
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }
}
