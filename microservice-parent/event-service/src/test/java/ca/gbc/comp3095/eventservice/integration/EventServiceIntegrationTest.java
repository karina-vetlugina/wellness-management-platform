package ca.gbc.comp3095.eventservice.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;
import org.hamcrest.Matchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventServiceIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    int port;

    static { postgres.start(); }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void registerAndUnregisterStudent() {
        long id = ((Number) RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                {"title":"Yoga","description":"Intro","eventDate":"2025-12-01T09:00:00",
                 "location":"Casa Loma","capacity":2}
                """)
                .post("/api/event")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        RestAssured.given().post("/api/event/{id}/register?studentId=101", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given().delete("/api/event/{id}/register?studentId=101", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }
}