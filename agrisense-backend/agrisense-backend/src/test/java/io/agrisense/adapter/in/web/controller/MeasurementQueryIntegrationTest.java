package io.agrisense.adapter.in.web.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class MeasurementQueryIntegrationTest {

    @Test
    public void testGetMeasurements_WithoutFilters_ReturnsPagedResult() {
        // Query measurements (assuming some exist from other tests or initial data)
        given()
            .when()
            .get("/api/measurements?page=1&size=10")
            .then()
            .statusCode(200)
            .body("content", notNullValue())
            .body("page", equalTo(1))
            .body("size", equalTo(10));
    }

    @Test
    public void testGetMeasurements_WithPagination() {
        // Query with specific pagination
        given()
            .when()
            .get("/api/measurements?page=1&size=2")
            .then()
            .statusCode(200)
            .body("content", notNullValue())
            .body("page", equalTo(1))
            .body("size", equalTo(2))
            .body("content.size()", lessThanOrEqualTo(2));
    }

    @Test
    public void testGetMeasurements_WithFieldFilter() {
        // Query by fieldId
        given()
            .when()
            .get("/api/measurements?fieldId=1&page=1&size=50")
            .then()
            .statusCode(200)
            .body("content", notNullValue());
    }

    @Test
    public void testGetMeasurements_WithInvalidPageNumber_UsesDefault() {
        given()
            .when()
            .get("/api/measurements?page=-1&size=10")
            .then()
            .statusCode(200)
            .body("page", equalTo(1)); // Should default to page 1
    }
}
