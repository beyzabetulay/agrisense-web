 package io.agrisense.adapter.in.web.dto;

import io.agrisense.domain.model.ECondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateAlertRuleRequest {
    
    @NotBlank(message = "Alert rule name is required")
    private String name;
    
    @NotNull(message = "Condition is required")
    private ECondition condition;

    
    @NotNull(message = "Threshold value is required")
    @Positive(message = "Threshold value must be positive")
    private Double threshold;
    
    private String description;
    
    public CreateAlertRuleRequest() {
    }
    
    public CreateAlertRuleRequest(String name, ECondition condition, Double threshold, String description) {
        this.name = name;
        this.condition = condition;
        this.threshold = threshold;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ECondition getCondition() {
        return condition;
    }
    
    public void setCondition(ECondition condition) {
        this.condition = condition;
    }
    
    public Double getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}