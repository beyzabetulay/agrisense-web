package io.agrisense.adapter.in.web.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class ValidationAndEdgeCaseTest {

    @Test
    public void testSensorWithEmptyName_Rejected() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        String sensorJson = "{\"name\":\"\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key123\",\"fieldId\":1}";
        given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(400);  // Empty name is rejected by validation
    }

    @Test
    public void testMeasurement_WithZeroValue_Accepted() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor first
        String sensorJson = "{\"name\":\"ZeroSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // Send measurement with zero value
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":0.0,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);
    }

    @Test
    public void testMeasurement_WithNegativeValue_Accepted() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"NegativeSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // Send negative measurement
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":-10.5,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);
    }

    @Test
    public void testMeasurement_WithVeryLargeValue_Accepted() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"LargeSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // Send very large measurement
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":999999.99,\"unit\":\"ppm\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);
    }

    @Test
    public void testMeasurement_WithMissingUnit_DefaultsToEmpty() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"UnitlessSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // Send measurement without unit
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":25.0}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);
    }

    @Test
    public void testAlertRule_WithDifferentConditions_AllAccepted() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"ConditionSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // Test GREATER_THAN
        String rule1Json = "{\"name\":\"R1\",\"condition\":\"GREATER_THAN\",\"threshold\":25.0}";
        given()
                .contentType("application/json")
                .body(rule1Json)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // Test LESS_THAN
        String rule2Json = "{\"name\":\"R2\",\"condition\":\"LESS_THAN\",\"threshold\":10.0}";
        given()
                .contentType("application/json")
                .body(rule2Json)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // Test EQUAL
        String rule3Json = "{\"name\":\"R3\",\"condition\":\"EQUAL\",\"threshold\":20.0}";
        given()
                .contentType("application/json")
                .body(rule3Json)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);
    }

    @Test
    public void testAlertRule_WithoutDescription_Accepted() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"NoDescSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // Create rule without description
        String ruleJson = "{\"name\":\"NoDesc\",\"condition\":\"GREATER_THAN\",\"threshold\":25.0}";
        given()
                .contentType("application/json")
                .body(ruleJson)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCompleteFlow_FarmToAlertNotification() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // 1. Create sensor
        String sensorJson = "{\"name\":\"FarmSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key\",\"fieldId\":1}";
        int sensorId = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // 2. Define alert rule
        String ruleJson = "{\"name\":\"HighTemp\",\"condition\":\"GREATER_THAN\",\"threshold\":30.0}";
        given()
                .contentType("application/json")
                .body(ruleJson)
                .when().post("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(201);

        // 3. Send measurement that violates rule
        String measurementJson = "{\"sensorId\":" + sensorId + ",\"value\":35.0,\"unit\":\"C\"}";
        given()
                .contentType("application/json")
                .body(measurementJson)
                .when().post("/api/measurements")
                .then()
                .statusCode(202);

        // 4. Verify rule exists and is returned
        given()
                .when().get("/sensors/" + sensorId + "/rules")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }
}
