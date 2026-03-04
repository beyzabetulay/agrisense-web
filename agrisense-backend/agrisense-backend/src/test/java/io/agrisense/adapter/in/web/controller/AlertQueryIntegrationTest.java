package io.agrisense.adapter.in.web.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AlertQueryIntegrationTest {

    @Test
    public void testGetAlertsWithoutFilter() {
        given()
            .when()
                .get("/api/alerts")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", notNullValue())
                .body("page", equalTo(1))
                .body("size", equalTo(50))
                .body("totalElements", greaterThanOrEqualTo(0))
                .body("totalPages", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetAlertsWithStatusFilter() {
        given()
            .queryParam("status", "OPEN")
            .when()
                .get("/api/alerts")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", notNullValue());
    }

    @Test
    public void testGetAlertsWithPagination() {
        given()
            .queryParam("page", 1)
            .queryParam("size", 10)
            .when()
                .get("/api/alerts")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("page", equalTo(1))
                .body("size", equalTo(10));
    }

    @Test
    public void testGetAlertsWithInvalidStatus() {
        given()
            .queryParam("status", "INVALID_STATUS")
            .when()
                .get("/api/alerts")
            .then()
                .statusCode(400);
    }

    @Test
    public void testGetAlertsWithAllFilters() {
        given()
            .queryParam("status", "OPEN")
            .queryParam("page", 1)
            .queryParam("size", 5)
            .when()
                .get("/api/alerts")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("page", equalTo(1))
                .body("size", equalTo(5));
    }
}
