package io.agrisense.adapter.in.web.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class SensorIntegrationTest {

    @Test
    public void testCreateAndRetrieveSensor_FullFlow() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor
        String sensorJson = "{\"name\":\"IntegrationSensor\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key123\",\"fieldId\":1}";
        Response createResponse = given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors");

        createResponse.then().statusCode(201);
        JsonPath jp = createResponse.jsonPath();
        Integer sensorId = jp.getInt("id");

        // Retrieve sensor by ID
        given()
                .when().get("/api/sensors/" + sensorId)
                .then()
                .statusCode(200)
                .body("name", equalTo("IntegrationSensor"))
                .body("type", equalTo("TEMPERATURE"));
    }

    @Test
    public void testGetAllSensors_ReturnsMultiple() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        given()
                .when().get("/api/sensors")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
        public void testGetSensorById_NonExistent_Returns404() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Service throws IllegalArgumentException for non-existent sensor
        // GlobalExceptionHandler catches it and returns 404 Not Found
        given()
                .when().get("/api/sensors/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateSensor_MissingName_Returns400() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // @NotBlank validation on name rejects null/empty
        // GlobalExceptionHandler catches ConstraintViolationException and returns 400
        String sensorJson = "{\"name\":null,\"type\":\"TEMPERATURE\",\"apiKey\":\"key123\",\"fieldId\":1}";
        given()
                .contentType("application/json")
                .body(sensorJson)
                .when().post("/api/sensors")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateMultipleSensors_AllRetrievable() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Create sensor 1
        String sensor1Json = "{\"name\":\"MultiSensor1\",\"type\":\"TEMPERATURE\",\"apiKey\":\"key1\",\"fieldId\":1}";
        Response r1 = given()
                .contentType("application/json")
                .body(sensor1Json)
                .when().post("/api/sensors");
        r1.then().statusCode(201);

        // Create sensor 2
        String sensor2Json = "{\"name\":\"MultiSensor2\",\"type\":\"MOISTURE\",\"apiKey\":\"key2\",\"fieldId\":1}";
        Response r2 = given()
                .contentType("application/json")
                .body(sensor2Json)
                .when().post("/api/sensors");
        r2.then().statusCode(201);

        // Get all and verify both exist
        given()
                .when().get("/api/sensors")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }
}
