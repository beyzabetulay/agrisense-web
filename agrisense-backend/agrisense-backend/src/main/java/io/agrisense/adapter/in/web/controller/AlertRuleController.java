package io.agrisense.adapter.in.web.controller;

import java.net.URI;
import java.util.List;

import io.agrisense.adapter.in.web.dto.AlertRuleResponse;
import io.agrisense.adapter.in.web.dto.CreateAlertRuleRequest;
import io.agrisense.adapter.in.web.mapper.AlertRuleWebMapper;
import io.agrisense.domain.model.AlertRule;
import io.agrisense.ports.in.IManageAlertRuleUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sensors/{sensorId}/rules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlertRuleController {
    
    private final IManageAlertRuleUseCase alertRuleUseCase;
    private final AlertRuleWebMapper alertRuleMapper;
    
    @Inject
    public AlertRuleController(IManageAlertRuleUseCase alertRuleUseCase, AlertRuleWebMapper alertRuleMapper) {
        this.alertRuleUseCase = alertRuleUseCase;
        this.alertRuleMapper = alertRuleMapper;
    }
    
    @POST
    public Response createAlertRule(
            @PathParam("sensorId") Long sensorId,
            @Valid CreateAlertRuleRequest request) {
        AlertRule ruleDomain = alertRuleMapper.toDomain(request);
        AlertRule createdRule = alertRuleUseCase.createRule(sensorId, ruleDomain);
        AlertRuleResponse responseDTO = alertRuleMapper.toResponse(createdRule);


        return Response
                .created(URI.create("/sensors/" + sensorId + "/rules/" + createdRule.getId()))
                .entity(responseDTO)
                .build();
    }
    
    @GET
    public Response getActiveAlertRules(@PathParam("sensorId") Long sensorId) {
        List<AlertRule> activeRules = alertRuleUseCase.getActiveRules(sensorId);
        List<AlertRuleResponse> responseList = alertRuleMapper.toResponseList(activeRules);
        
        return Response.ok(responseList).build();
    }

    @PUT
    @Path("/{ruleId}")
    public Response updateAlertRule(
            @PathParam("sensorId") Long sensorId,
            @PathParam("ruleId") Long ruleId,
            @Valid CreateAlertRuleRequest request) {
        
        AlertRule ruleDomain = alertRuleMapper.toDomain(request);
        AlertRule updated = alertRuleUseCase.updateRule(ruleId, ruleDomain);
        AlertRuleResponse responseDTO = alertRuleMapper.toResponse(updated);
        return Response.ok(responseDTO).build();
    }

    @DELETE
    @Path("/{ruleId}")
    public Response deleteAlertRule(
            @PathParam("sensorId") Long sensorId,
            @PathParam("ruleId") Long ruleId) {
        
        alertRuleUseCase.deleteRule(ruleId);
        return Response.noContent().build();
    }
}