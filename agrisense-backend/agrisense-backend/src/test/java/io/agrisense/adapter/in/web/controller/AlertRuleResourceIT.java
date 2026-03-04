package io.agrisense.adapter.in.web.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

//import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class AlertRuleResourceIT {

    @Test
    public void testCreateRuleFlow() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create a sensor first
        String sensorJson = "{\"name\":\"IT-Sensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"it-key\",\"fieldId\":1}";
        Response createSensor = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors");

        createSensor.then().statusCode(201);
        JsonPath jp = createSensor.jsonPath();
        Integer sensorId = jp.getInt("id");

        // Create an alert rule for that sensor
        String ruleJson = "{\"name\":\"IT-HighTemp\",\"condition\":\"GREATER_THAN\",\"value\":25.0,\"description\":\"it\"}";
        given()
                .contentType("application/json")
                .body(ruleJson)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // Retrieve rules
        given()
                .when().get("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testCreateAndRetrieveAlertRule_FullFlow() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"RuleSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        Response sensorResp = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors");

        JsonPath jp = sensorResp.jsonPath();
        Integer sensorId = jp.getInt("id");

        // Create rule
        String ruleJson = "{\"name\":\"TestRule\",\"condition\":\"GREATER_THAN\",\"value\":20.0,\"description\":\"test\"}";
        Response ruleResp = given()
                .contentType("application/json")
                .body(ruleJson)
                .when().post("/sensors/" + sensorId + "/rules");

        ruleResp.then().statusCode(201);
        JsonPath ruleJp = ruleResp.jsonPath();
        Integer ruleId = ruleJp.getInt("id");

        // Retrieve rule
        given()
                .when().get("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(200)
                .body("findAll { it.id == " + ruleId + " }.size()", greaterThan(0));
    }

    @Test
    public void testCreateRuleWithoutSensor_Returns404() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        String ruleJson = "{\"name\":\"TestRule\",\"condition\":\"GREATER_THAN\",\"value\":20.0}";
        given()
                .contentType("application/json")
                .body(ruleJson)
                .when().post("/sensors/99999/rules")
                .then()
                .statusCode(404);
    }

    @Test
    public void testMultipleRulesPerSensor_AllRetrievable() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"MultiRuleSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        Response sensorResp = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors");

        JsonPath jp = sensorResp.jsonPath();
        Integer sensorId = jp.getInt("id");

        // Create rule 1
        String rule1Json = "{\"name\":\"Rule1\",\"condition\":\"GREATER_THAN\",\"value\":25.0}";
        given()
                .contentType("application/json")
                .body(rule1Json)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // Create rule 2
        String rule2Json = "{\"name\":\"Rule2\",\"condition\":\"LESS_THAN\",\"value\":10.0}";
        given()
                .contentType("application/json")
                .body(rule2Json)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // Retrieve rules
        given()
                .when().get("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }
}
