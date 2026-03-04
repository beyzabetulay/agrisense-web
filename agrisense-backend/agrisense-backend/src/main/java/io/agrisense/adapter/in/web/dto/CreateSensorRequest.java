package io.agrisense.adapter.in.web.dto;

import io.agrisense.domain.model.ESensorType;
import io.agrisense.domain.model.ESensorStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateSensorRequest {

    @NotBlank(message = "Sensor name cannot be empty")
    @Size(min = 3, max = 50, message = "Sensor name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Sensor type is required")
    private ESensorType type;

    // Optional — defaults to ACTIVE if not provided
    private ESensorStatus status;

    @NotBlank(message = "API Key is required")
    private String apiKey;

    @NotNull(message = "Field ID is required")
    @Positive(message = "Field ID must be a positive number")
    private Long fieldId;

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