package io.agrisense.adapter.in.web.mapper;

import java.util.List;
import java.util.stream.Collectors;

import io.agrisense.adapter.in.web.dto.AlertRuleResponse;
import io.agrisense.adapter.in.web.dto.CreateAlertRuleRequest;
import io.agrisense.domain.model.AlertRule;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlertRuleWebMapper {

    public AlertRule toDomain(CreateAlertRuleRequest request) {
        if (request == null) return null;
        
        AlertRule rule = new AlertRule();
        rule.setRuleName(request.getName());
        rule.setCondition(request.getCondition());
        rule.setThreshold(request.getThreshold()); 
        
        rule.setActive(true);
        
        return rule;
    }

    public AlertRuleResponse toResponse(AlertRule domain) {
        if (domain == null) return null;

        return new AlertRuleResponse(
            domain.getId(),
            domain.getSensorId(),
            domain.getRuleName(),
            domain.getCondition(),
            domain.getThreshold(),
            domain.isActive()
        );
    }

    public List<AlertRuleResponse> toResponseList(List<AlertRule> rules) {
        return rules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}