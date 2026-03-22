package ca.gbc.comp3095.apigateway;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
//import jakarta.ws.rs.core.Response;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.json.JSONObject;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {


    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");


    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:24.0.1")
            .withRealmImportFile("realm-export.json")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withExposedPorts(8080)
            .waitingFor(
                    Wait.forHttp("/realms/master")
                            .forPort(8080)
                            .withStartupTimeout(Duration.ofMinutes(3))
            );
//            .waitingFor(
//                    Wait.forHttp("/health/ready")
//                            .forStatusCode(200)
//                            .withStartupTimeout(Duration.ofMinutes(1))
//            );

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        postgreSQLContainer.start();
//        keycloakContainer.start();

//        System.setProperty("spring.redis.host", keycloakContainer.getHost());
//        System.setProperty("spring.redis.port", keycloakContainer.getFirstMappedPort().toString());
    }


    private static final String REALM_NAME = "spring-microservices-security-realm";

    private static final String TEST_USER_USERNAME = "admin";
    private static final String TEST_USER_PASSWORD = "admin";
    private static final String TEST_ROLE_NAME = "admin";

    private static final String USER_CLIENT_ID = "spring-client-credentials-id";
    //    private static final String USER_CLIENT_SECRET = "test-secret";
    private static final String SERVICE_CLIENT_ID = "service-client";
    private static final String SERVICE_CLIENT_SECRET = "service-secret";
    private static final String KEYCLOAK_TOKEN_PATH = "/realms/" + REALM_NAME + "/protocol/openid-connect/token";

    // --- Keycloak Admin Setup ---

    /**
     * Helper to create the Keycloak Admin Client.
     */
    private static Keycloak createKeycloakClient() {
//        Keycloak keycloakAdminClient = keycloakContainer.getKeycloakAdminClient();

        return Keycloak.getInstance(
                keycloakContainer.getAuthServerUrl(),
                REALM_NAME,
                "admin",
                "admin",
                "admin-cli"
        );
//        return Keycloak.getInstance(
//                keycloakContainer.getAuthServerUrl(),
//                "master",
//                keycloakContainer.getAdminUsername(),
//                keycloakContainer.getAdminPassword(),
//                "admin-cli"
//        );
    }

    /**
     * Initializes users and assigns roles in the Keycloak realm before any test runs.
     * Assumes the role 'TEST_ROLE_NAME' already exists from the imported realm-export.json.
     */
    @BeforeAll
    static void setupRealm() {
        try (Keycloak keycloak = createKeycloakClient()) {
            RealmResource realmResource = keycloak.realm(REALM_NAME);

            // 1. Fetch the Role (must exist from the imported realm file)
            RoleRepresentation userRole = realmResource.roles().get(TEST_ROLE_NAME).toRepresentation();
            System.out.println("Fetched existing role for assignment: " + userRole.getName());

            // 2. Create User
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(TEST_USER_PASSWORD);

            UserRepresentation user = new UserRepresentation();
            user.setUsername(TEST_USER_USERNAME);
            user.setEnabled(true);
            user.setCredentials(List.of(passwordCred));
            user.setFirstName("Test");
            user.setLastName("User");
            user.setEmail("test@example.com");

            String userId = "";
            try {
                // Create user
                jakarta.ws.rs.core.Response response = realmResource.users().create(user);
                // Extract user ID from the response header
                String location = response.getHeaderString("Location");
                userId = location.substring(location.lastIndexOf('/') + 1);
                System.out.println("Created user '" + TEST_USER_USERNAME + "' with ID: " + userId);
                response.close();
            } catch (Exception e) {
//                // User might already exist (e.g., from realm-export.json). Find existing user ID.
//                userId = realmResource.users().searchForUser(TEST_USER_USERNAME, true).stream()
//                        .filter(u -> u.getUsername().equals(TEST_USER_USERNAME))
//                        .findFirst().orElseThrow(() -> new IllegalStateException("Test user not found.")).getId();
                System.out.println("User '" + TEST_USER_USERNAME + "' already exists with ID: " + userId);
            }

            // 3. Assign Role to User
            realmResource.users().get(userId).roles().realmLevel().add(List.of(userRole));
            System.out.println("Assigned role '" + TEST_ROLE_NAME + "' to user '" + TEST_USER_USERNAME + "'.");

        } catch (Exception e) {
            System.err.println("Failed to setup Keycloak realm for testing: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Keycloak setup failed.", e);
        }
    }


    // --------------------------------------------------------------------------------
    // --- Utility Methods to Fetch Tokens ---
    // --------------------------------------------------------------------------------

    /**
     * Simulates a user logging in (often used to validate the Authorization Code Flow setup
     * in tests by using the Password Grant Type).
     *
     * @return The access token string.
     */
    private String getAccessTokenUser() throws JSONException {
        // Keycloak uses the mapped port from the Testcontainer
        String keycloakUrl = keycloakContainer.getAuthServerUrl() + KEYCLOAK_TOKEN_PATH;

        Response response = RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(Map.of(
                        "client_id", USER_CLIENT_ID,
//                        "client_secret", USER_CLIENT_SECRET,
                        "username", "test-user", // User must exist in realm-export.json
                        "password", "test-password", // User must exist in realm-export.json
                        "grant_type", "password",
                        "scope", "openid"
                ))
                .post(keycloakUrl);

        response.then().statusCode(HttpStatus.OK.value());

        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        return jsonResponse.getString("access_token");
    }

    /**
     * Obtains a token using the Client Credentials Flow (for machine-to-machine/service calls).
     *
     * @return The access token string.
     */
    private String getAccessTokenClientCredentials() throws JSONException {
        String keycloakUrl = keycloakContainer.getAuthServerUrl() + KEYCLOAK_TOKEN_PATH;

        Response response = RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(Map.of(
                        "client_id", SERVICE_CLIENT_ID, // Client must exist in realm-export.json
                        "client_secret", SERVICE_CLIENT_SECRET, // Client must exist in realm-export.json
                        "grant_type", "client_credentials",
                        "scope", "openid"
                ))
                .post(keycloakUrl);

        response.then().statusCode(HttpStatus.OK.value());

        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        return jsonResponse.getString("access_token");
    }


    // --------------------------------------------------------------------------------
    // --- Test Cases ---
    // --------------------------------------------------------------------------------

    @Test
    void contextLoads() {
        // Ensures Spring Boot context and containers start correctly
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        assertThat(keycloakContainer.isRunning()).isTrue();
    }

    /**
     * Validates Keycloak issues a token using the Password Grant (simulating the Authorization Code setup).
     */
    @Test
    void shouldSuccessfullyObtainUserAccessToken() throws JSONException {
        String userToken = getAccessTokenUser();
        assertNotNull(userToken, "User access token should not be null");

        // Optional: If you had a secured endpoint, you would call it here:
        // RestAssured.given()
        //         .auth().oauth2(userToken)
        //         .when().get("/api/secure/user-endpoint")
        //         .then().statusCode(HttpStatus.OK.value());
    }

    /**
     * Validates Keycloak issues a token using the Client Credentials Grant (Service-to-Service).
     */
    @Test
    void shouldSuccessfullyObtainClientCredentialsAccessToken() throws JSONException {
        String clientToken = getAccessTokenClientCredentials();
        assertNotNull(clientToken, "Client Credentials access token should not be null");

        // Optional: If you had a secured endpoint, you would call it here:
        // RestAssured.given()
        //         .auth().oauth2(clientToken)
        //         .when().get("/api/secure/service-endpoint")
        //         .then().statusCode(HttpStatus.OK.value());
    }

    /**
     * Validates token issuance failure with bad credentials.
     */
    @Test
    void shouldFailToObtainAccessTokenWithBadUserCredentials() {
        String keycloakUrl = keycloakContainer.getAuthServerUrl() + KEYCLOAK_TOKEN_PATH;

        RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(Map.of(
                        "client_id", USER_CLIENT_ID,
//                        "client_secret", USER_CLIENT_SECRET,
                        "username", "bad-user",
                        "password", "wrong-password",
                        "grant_type", "password",
                        "scope", "openid"
                ))
                .post(keycloakUrl)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value()) // Expect 401 Unauthorized
                .body("error", org.hamcrest.Matchers.equalTo("invalid_grant"));
    }


}
