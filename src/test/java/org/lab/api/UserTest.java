package org.lab.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.lab.AbstractTest;
import org.lab.api.dto.authentification.AuthorizationAnswer;
import org.lab.api.dto.authentification.AuthorizationRequest;
import org.lab.api.dto.authentification.RegisterRequest;
import org.lab.api.dto.authentification.SessionUpdateRequest;
import org.lab.data.entity.UserType;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest extends AbstractTest {

    private String accessToken;
    private String refreshToken;

    @Test
    @Order(0)
    void createUserTest() {
        Instant time = Instant.parse("2020-01-01T20:20:00Z");
        clock.setTime(time);
        var result = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("user", "password"))
                .when()
                .post("/api/register")
                .then()
                .statusCode(201)
                .extract().body().as(AuthorizationAnswer.class);
        assertThat(result.user().userType()).isEqualTo(UserType.DEFAULT);
        assertThat(result.user().name()).isEqualTo("user");
        var token = jwtService.decodeToken(result.accessToken());
        assertThat(token.getIssuedAt()).isEqualTo(time);
        this.accessToken = result.accessToken();
        this.refreshToken = result.refreshToken();
    }

    @Test
    @Order(1)
    void refreshTest() {
        Instant time = Instant.parse("2021-01-01T20:20:00Z");
        clock.setTime(time);
        var result = given()
                .contentType(ContentType.JSON)
                .body(new SessionUpdateRequest(refreshToken))
                .when()
                .post("/api/auth/updateToken")
                .then()
                .statusCode(200)
                .extract().body().as(AuthorizationAnswer.class);
        assertThat(result.user().userType()).isEqualTo(UserType.DEFAULT);
        assertThat(result.user().name()).isEqualTo("user");
        var token = jwtService.decodeToken(result.accessToken());
        assertThat(token.getIssuedAt()).isEqualTo(time);
    }

    @Test
    @Order(2)
    void authTest() {
        Instant time = Instant.parse("2021-01-01T20:20:00Z");
        clock.setTime(time);
        var result = given()
                .contentType(ContentType.JSON)
                .body(new AuthorizationRequest("user", "password"))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200)
                .extract().body().as(AuthorizationAnswer.class);
        assertThat(result.user().userType()).isEqualTo(UserType.DEFAULT);
        assertThat(result.user().name()).isEqualTo("user");
        var token = jwtService.decodeToken(result.accessToken());
        assertThat(token.getIssuedAt()).isEqualTo(time);
    }

}
