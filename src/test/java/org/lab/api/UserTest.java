package org.lab.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lab.AbstractTest;
import org.lab.api.dto.AuthorizationAnswer;
import org.lab.api.dto.RegisterRequest;
import org.lab.data.entity.UserType;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest extends AbstractTest {

    @Test
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
    }

}
