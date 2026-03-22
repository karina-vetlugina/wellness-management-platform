package ca.gbc.comp3095.goaltrackingservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoalTrackingServiceApplicationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @LocalServerPort
    private Integer port;

    static {
        mongoDBContainer.start();
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // ---------- helper ----------
    private String createGoalAndReturnId(String title, String description, String targetDate,
                                         String status, String category) {
        String requestBody = """
        {
          "title": "%s",
          "description": "%s",
          "targetDate": "%s",
          "status": "%s",
          "category": "%s"
        }
        """.formatted(title, description, targetDate, status, category);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/goal")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
    }

    // ---------- CRUD ITs ----------

    @Test
    void createGoalTest() {
        String requestBody = """
        {
          "title": "Daily Meditation",
          "description": "10 minutes of guided breathing",
          "targetDate": "2025-12-01",
          "status": "IN_PROGRESS",
          "category": "Mindfulness"
        }
        """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/goal")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("title", Matchers.equalTo("Daily Meditation"))
                .body("description", Matchers.equalTo("10 minutes of guided breathing"))
                .body("targetDate", Matchers.equalTo("2025-12-01"))
                .body("status", Matchers.equalTo("IN_PROGRESS"))
                .body("category", Matchers.equalTo("Mindfulness"));
    }

    @Test
    void getAllGoalsTest() {
        String seedBody = """
        {
          "title": "Evening Journaling",
          "description": "3 prompts for reflection",
          "targetDate": "2025-12-05",
          "status": "IN_PROGRESS",
          "category": "Therapy"
        }
        """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(seedBody)
                .when()
                .post("/api/goal")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured.given()
                .when()
                .get("/api/goal")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("[0].id", Matchers.notNullValue());
    }

    @Test
    void updateGoalTest() {
        String id = createGoalAndReturnId(
                "Progressive Muscle Relaxation",
                "Short PMR routine",
                "2025-12-10",
                "IN_PROGRESS",
                "Mindfulness"
        );

        String updateBody = """
        {
          "id": "%s",
          "title": "Progressive Muscle Relaxation",
          "description": "Extended PMR routine (15 minutes)",
          "targetDate": "2025-12-12",
          "status": "IN_PROGRESS",
          "category": "Mindfulness"
        }
        """.formatted(id);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/goal/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", "/api/goal/" + id);

        RestAssured.given()
                .when()
                .get("/api/goal")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("find { it.id == '%s' }.description".formatted(id),
                        Matchers.equalTo("Extended PMR routine (15 minutes)"))
                .body("find { it.id == '%s' }.targetDate".formatted(id),
                        Matchers.equalTo("2025-12-12"));
    }

    @Test
    void deleteGoalTest() {
        String id = createGoalAndReturnId(
                "Sleep Hygiene",
                "No screens 60 minutes before bed",
                "2025-12-20",
                "IN_PROGRESS",
                "Sleep"
        );

        RestAssured.given()
                .when()
                .get("/api/goal")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.hasItem(id));

        RestAssured.given()
                .when()
                .delete("/api/goal/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given()
                .when()
                .get("/api/goal")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.not(Matchers.hasItem(id)));
    }
}