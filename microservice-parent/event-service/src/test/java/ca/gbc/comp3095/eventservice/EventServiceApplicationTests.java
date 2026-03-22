package ca.gbc.comp3095.eventservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventServiceApplicationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private Integer port;

    static{
        postgres.start();
    }

    @BeforeEach
    void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private long createEventAndReturnId(String title, String description, String eventDateIso, String location, int capacity){
        String requestBody = """
                {
                    "title": "%s",
                    "description": "%s",
                    "eventDate": "%s",
                    "location": "%s",
                    "capacity": %d
                }
                """.formatted(title, description, eventDateIso, location, capacity);

        return ((Number) RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/event")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id")).longValue();
    }

    @Test
    void createEventTest(){
        String requestBody = """
                {
                    "title": "Mindfulness Workshop",
                    "description": "Meditation",
                    "eventDate": "2025-10-12T10:00:00",
                    "location": "Casa Loma Campus",
                    "capacity": 30
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/event")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("title", Matchers.equalTo("Mindfulness Workshop"))
                .body("description", Matchers.equalTo("Meditation"))
                .body("eventDate", Matchers.startsWith("2025-10-12T10:00:00"))
                .body("location", Matchers.equalTo("Casa Loma Campus"))
                .body("capacity", Matchers.equalTo(30));
    }

    @Test
    void getAllEventsTest(){
        // ensure there is at least 1 event
        createEventAndReturnId(
                "Yoga Basics",
                "Intro session",
                "2025-10-15T09:00:00",
                "Toronto",
                20
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/event")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0));
    }

    @Test
    void updateEventTest(){
        long id = createEventAndReturnId(
                "Some title",
                "Some description",
                "2025-10-20T14:00:00",
                "Ottawa",
                25
        );

        String updatedBody = """
                {
                    "title": "Another title",
                    "description": "Another description",
                    "eventDate": "2025-10-20T15:30:00",
                    "location": "Toronto",
                    "capacity": 35
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updatedBody)
                .when()
                .put("/api/event/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", "/api/event/" + id);

        // verify via GET by id
        RestAssured.given()
                .when()
                .get("/api/event/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo((int) id))
                .body("title", Matchers.equalTo("Another title"))
                .body("capacity", Matchers.equalTo(35));
    }

    @Test
    void deleteEventTest(){
        long id = createEventAndReturnId(
                "Disposable",
                "Remove me",
                "2025-10-25T18:00:00",
                "GBC",
                111
        );

        // ensure it exists
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/event")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.hasItem((int) id));

        // delete
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/event/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // verify it no longer exists in the list
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/event")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.not(Matchers.hasItem((int) id)));
    }

}
