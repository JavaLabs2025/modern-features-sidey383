package org.lab.api;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lab.AbstractAuthorizedTest;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.bug.BugReportAnswer;
import org.lab.api.dto.bug.BugReportCreateRequest;
import org.lab.data.entity.BugReportStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BugReportControllerTest extends AbstractAuthorizedTest {

    @Test
    public void testCreateAndGetBugReport() {
        BugReportAnswer created = given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .body(new BugReportCreateRequest(project.id(), "Bug #1", "Something broken"))
                .when()
                .post("/api/bug-report")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        assertThat(created.name()).isEqualTo("Bug #1");
        assertThat(created.description()).isEqualTo("Something broken");
        assertThat(created.status()).isEqualTo(BugReportStatus.NEW);

        BugReportAnswer fetched = given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .queryParam("bugReportId", created.id())
                .when()
                .get("/api/bug-report")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.name()).isEqualTo(created.name());
        assertThat(fetched.description()).isEqualTo(created.description());
    }

    @Test
    public void testGetBugReportsList() {
        BugReportAnswer b1 = given()
                .contentType(ContentType.JSON)
                .header("Authorization", tester.accessToken())
                .body(new BugReportCreateRequest(project.id(), "Bug A", "Desc A"))
                .when()
                .post("/api/bug-report")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        BugReportAnswer b2 = given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .body(new BugReportCreateRequest(project.id(), "Bug B", "Desc B"))
                .when()
                .post("/api/bug-report")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        ListAnswer<BugReportAnswer> list = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", project.id())
                .when()
                .get("/api/bug-reports")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });

        List<BugReportAnswer> items = List.copyOf(list.getItems());
        assertThat(items).extracting(BugReportAnswer::id).contains(b1.id(), b2.id());
    }

    @Test
    public void testBugReportStatusWorkflow() {
        BugReportAnswer created = given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .body(new BugReportCreateRequest(project.id(), "Workflow Bug", "Work flow description"))
                .when()
                .post("/api/bug-report")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        long bugId = created.id();

        BugReportAnswer fixed = given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .queryParam("bugReportId", bugId)
                .when()
                .post("/api/bug-report/fix")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        assertThat(fixed.status()).isEqualTo(BugReportStatus.FIXED);

        BugReportAnswer tested = given()
                .contentType(ContentType.JSON)
                .header("Authorization", tester.accessToken())
                .queryParam("bugReportId", bugId)
                .when()
                .post("/api/bug-report/tested")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        assertThat(tested.status()).isEqualTo(BugReportStatus.TESTED);

        BugReportAnswer closed = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("bugReportId", bugId)
                .when()
                .post("/api/bug-report/close")
                .then()
                .statusCode(200)
                .extract().body().as(BugReportAnswer.class);

        assertThat(closed.status()).isEqualTo(BugReportStatus.CLOSED);
    }
}
