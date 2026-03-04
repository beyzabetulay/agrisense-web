package io.agrisense.adapter.in.web.mapper;

import io.agrisense.adapter.in.web.dto.CreateAlertRuleRequest;
import io.agrisense.adapter.in.web.dto.AlertRuleResponse;
import io.agrisense.domain.model.AlertRule;
import io.agrisense.domain.model.ECondition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertRuleWebMapperTest {

    private AlertRuleWebMapper mapper = new AlertRuleWebMapper();

    @Test
    public void testToDomain_FromValidRequest_MapsAllFields() {
        CreateAlertRuleRequest req = new CreateAlertRuleRequest("HighTemp", ECondition.GREATER_THAN, 30.0, "desc");

        AlertRule domain = mapper.toDomain(req);

        assertNotNull(domain);
        assertEquals("HighTemp", domain.getRuleName());
        assertEquals(ECondition.GREATER_THAN, domain.getCondition());
        assertEquals(30.0, domain.getThreshold());
        assertTrue(domain.isActive());
    }

    @Test
    public void testToResponse_FromValidDomain_MapsAllFields() {
        AlertRule rule = new AlertRule();
        rule.setId(5L);
        rule.setSensorId(1L);
        rule.setRuleName("HighTemp");
        rule.setCondition(ECondition.GREATER_THAN);
        rule.setThreshold(30.0);
        rule.setActive(true);

        AlertRuleResponse resp = mapper.toResponse(rule);

        assertNotNull(resp);
        assertEquals(5L, resp.getId());
        assertEquals(1L, resp.getSensorId());
        assertEquals("HighTemp", resp.getRuleName());
        assertEquals(ECondition.GREATER_THAN, resp.getCondition());
        assertEquals(30.0, resp.getThreshold());
        assertTrue(resp.isActive());
    }

    @Test
    public void testToResponseList_ConvertsList() {
        AlertRule r1 = new AlertRule();
        r1.setId(1L);
        r1.setRuleName("Rule1");
        AlertRule r2 = new AlertRule();
        r2.setId(2L);
        r2.setRuleName("Rule2");

        List<AlertRule> rules = Arrays.asList(r1, r2);
        List<AlertRuleResponse> responses = mapper.toResponseList(rules);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Rule1", responses.get(0).getRuleName());
        assertEquals("Rule2", responses.get(1).getRuleName());
    }

    @Test
    public void testToResponseList_EmptyList() {
        List<AlertRuleResponse> responses = mapper.toResponseList(Collections.emptyList());

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testToDomain_WithNullInput_ReturnsNull() {
        AlertRule domain = mapper.toDomain(null);

        assertNull(domain);
    }

    @Test
    public void testToResponse_WithNullInput_ReturnsNull() {
        AlertRuleResponse resp = mapper.toResponse(null);

        assertNull(resp);
    }
}
