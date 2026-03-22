package ca.gbc.comp3095.wellnessservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WellnessServiceApplicationTests {

    String categoryTitle = "category_title1";
    String categoryDescription = "category_description1";

    String resourceTitle = "resource_title1";
    String resourceDescription = "resource_description1";
    String resourceUrl = "resource_url1";

    String categoryRequestBody = "";
    String resourceRequestBody = "";

    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
    @ServiceConnection
    static GenericContainer<?> redisContainer = new GenericContainer("redis:latest").withExposedPorts(6379);

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        postgreSQLContainer.start();
        redisContainer.start();

        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getFirstMappedPort().toString());
    }


// ------------- Testing Category -------------

    @Test
    void createCategoryTest() {
        String categoryId = createCategoryAndReturnId();
        assertNotNull(categoryId, "Category ID should not be null");
        assertFalse(categoryId.isBlank(), "Category ID should not be blank");
    }

    @Test
    void getAllCategoriesTest() {
        String categoryId = createCategoryAndReturnId();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/category")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("[0].title", Matchers.is(categoryTitle))
                .body("[0].description", Matchers.is(categoryDescription));
    }

    @Test
    void updateCategoryTest() {
        String categoryId = createCategoryAndReturnId();
        String modifiedCategoryDescription = categoryDescription + "_modified";
        createUpdateCategoryRequestBody(categoryId, categoryTitle, modifiedCategoryDescription);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(categoryRequestBody)
                .when()
                .put("/api/category/{id}", categoryId)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", "/api/category/" + categoryId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/category")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("find { it.id == '%s' }.title".formatted(categoryId), Matchers.is(categoryTitle))
                .body("find { it.id == '%s' }.description".formatted(categoryId), Matchers.is(modifiedCategoryDescription));

    }

    @Test
    void deleteCategoryTest() {
        String categoryId = createCategoryAndReturnId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/category/{id}", categoryId)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/category")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.not(Matchers.hasItem(categoryId)));
    }


// ------------- Testing Wellness Resource -------------

    @Test
    void createWellnessResourceTest() {
        String resourceId = createWellnessResourceAndReturnId();
        assertNotNull(resourceId, "Wellness resource ID should not be null");
        assertFalse(resourceId.isBlank(), "Wellness resource ID should not be blank");
    }

    @Test
    void findAllWellnessResourceTest() {
        String resourceId = createWellnessResourceAndReturnId();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/wellness-resource")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("[0].title", Matchers.is(resourceTitle))
                .body("[0].description", Matchers.is(resourceDescription))
                .body("[0].categoryTitle", Matchers.is(categoryTitle));
    }

    @Test
    void findResourceByIdTest(){
        String resourceId = createWellnessResourceAndReturnId();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(resourceRequestBody)
                .when()
                .get("/api/wellness-resource/{id}", resourceId)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("title", Matchers.is(resourceTitle))
                .body("description", Matchers.is(resourceDescription))
                .body("categoryTitle", Matchers.is(categoryTitle))
                .body("url", Matchers.is(resourceUrl));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/wellness-resource/{id}", "test_to_fail")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo("Invalid resource ID: " + "test_to_fail"));
    }

    @Test
    void updateWellnessResourceTest() {
        String resourceId = createWellnessResourceAndReturnId();
        String modifiedTitle = resourceTitle + "_modified";
        createUpdateWellnessRequestBody(resourceId, modifiedTitle, resourceDescription, resourceUrl);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(resourceRequestBody)
                .when()
                .put("/api/wellness-resource/{id}", resourceId)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", "/api/wellness-resource/" + resourceId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/wellness-resource")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("find { it.id == '%s' }.title".formatted(resourceId), Matchers.is(modifiedTitle))
                .body("find { it.id == '%s' }.description".formatted(resourceId), Matchers.is(resourceDescription))
                .body("find { it.id == '%s' }.url".formatted(resourceId), Matchers.is(resourceUrl))
                .body("find { it.id == '%s' }.categoryTitle".formatted(resourceId), Matchers.is(categoryTitle));
    }

    @Test
    void deleteWellnessResourceTest() {
        String resourceId = createWellnessResourceAndReturnId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/wellness-resource/{id}", resourceId)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/wellness-resource")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.not(Matchers.hasItem(resourceId)));
    }

// ------------- Helper functions  -------------

    private void createUpdateCategoryRequestBody(String categoryId, String categoryTitle, String categoryDescription) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put("id", categoryId);
        requestJson.put("title", categoryTitle);
        requestJson.put("description", categoryDescription);
        this.categoryRequestBody = requestJson.toString();
    }

    private void createUpdateWellnessRequestBody(String resourceId, String resourceTitle, String resourceDescription, String resourceUrl) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestJson = objectMapper.createObjectNode();

        String categoryId = createCategoryAndReturnId();
        requestJson.put("id", resourceId);
        requestJson.put("title", resourceTitle);
        requestJson.put("description", resourceDescription);
        requestJson.put("url", resourceUrl);
        requestJson.put("categoryId", categoryId);

        this.resourceRequestBody = requestJson.toString();
    }

    private String createWellnessResourceAndReturnId() {
        createUpdateWellnessRequestBody(null, resourceTitle, resourceDescription, resourceUrl);
        String id = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(resourceRequestBody)
                .when()
                .post("/api/wellness-resource")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
        createUpdateWellnessRequestBody(id, resourceTitle, resourceDescription, resourceUrl);
        return id;
    }

    private String createCategoryAndReturnId() {
        createUpdateCategoryRequestBody(null, categoryTitle, categoryDescription);
        String id = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(categoryRequestBody)
                .when()
                .post("/api/category")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
        createUpdateCategoryRequestBody(id, categoryTitle, categoryDescription);
        return id;
    }

}
