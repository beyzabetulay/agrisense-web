package io.agrisense.adapter.in.web.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

//import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class MeasurementIntegrationTest {

    @Test
    public void testPostMeasurement_WithValidSensor_Processes() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // First create a sensor
        String sensorJson = "{\"name\":\"MeasSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key123\",\"fieldId\":1}";
        Response sensorResp = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors");

        sensorResp.then().statusCode(201);
        JsonPath jp = sensorResp.jsonPath();
        Integer sensorId = jp.getInt("id");

        // Post measurement for sensor
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":25.5,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);
    }

    @Test
    public void testPostMeasurement_WithInvalidSensor_Returns404() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        String measurementJson = "{\"sensorId\":99999,\"value\":25.5,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(404);
    }

    @Test
    public void testPostMeasurement_MissingValue_Returns400() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        String measurementJson = "{\"sensorId\":1,\"value\":null,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(400);
    }

    @Test
    public void testPostMeasurement_MissingSensorId_Returns400() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        String measurementJson = "{\"sensorId\":null,\"value\":25.5,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(400);
    }

    @Test
    public void testMeasurementProcessing_TriggersAlertRuleEvaluation() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"AlertTestSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key123\",\"fieldId\":1}";
        Response sensorResp = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors");

        JsonPath jp = sensorResp.jsonPath();
        Integer sensorId = jp.getInt("id");

        // Create alert rule (threshold 25)
        String ruleJson = "{\"name\":\"HighTemp\",\"condition\":\"GREATER_THAN\",\"threshold\":25.0,\"description\":\"test\"}";
        given()
                .contentType("application/json")
                .body(ruleJson)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // Send measurement that violates rule (30 > 25)
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":30.0,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);
    }
}
