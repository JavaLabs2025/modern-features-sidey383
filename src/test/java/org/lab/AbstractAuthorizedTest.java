package org.lab;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.lab.api.dto.CreateProjectRequest;
import org.lab.api.dto.ProjectAnswer;
import org.lab.api.dto.ProjectUserUpdateRequest;
import org.lab.api.dto.authentification.AuthorizationAnswer;
import org.lab.api.dto.authentification.RegisterRequest;

import java.time.Instant;

import static io.restassured.RestAssured.given;

public class AbstractAuthorizedTest extends AbstractTest {

    private static final Instant time = Instant.parse("2020-01-01T20:20:00Z");
    protected static AuthorizationAnswer manager;
    protected static AuthorizationAnswer developer;
    protected static AuthorizationAnswer tester;
    protected static AuthorizationAnswer teamLead;
    protected static ProjectAnswer project;

    @Order(1)
    @BeforeAll
    public static void setupUsers() {
        clock.setTime(time);
        manager = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("manager_user", "password"))
                .when()
                .post("/api/register")
                .then()
                .statusCode(201)
                .extract().body().as(AuthorizationAnswer.class);
        developer = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("programmer_user", "password"))
                .when()
                .post("/api/register")
                .then()
                .statusCode(201)
                .extract().body().as(AuthorizationAnswer.class);

        tester = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("tester_user", "password"))
                .when()
                .post("/api/register")
                .then()
                .statusCode(201)
                .extract().body().as(AuthorizationAnswer.class);

        teamLead = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("teamLead_user", "password"))
                .when()
                .post("/api/register")
                .then()
                .statusCode(201)
                .extract().body().as(AuthorizationAnswer.class);
        project = given()
                .header("Authorization", manager.accessToken())
                .contentType(ContentType.JSON)
                .body(new CreateProjectRequest("Some project"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(project.id(), teamLead.user().id(), null))
                .when()
                .post("/api/project/teamlead")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(project.id(), developer.user().id(), null))
                .when()
                .post("/api/project/developer")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(project.id(), tester.user().id(), null))
                .when()
                .post("/api/project/tester")
                .then()
                .statusCode(200);
    }

}
