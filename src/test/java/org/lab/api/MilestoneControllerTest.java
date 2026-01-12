package org.lab.api;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lab.AbstractAuthorizedTest;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.milestone.MilestoneAnswer;
import org.lab.api.dto.milestone.MilestoneCreateRequest;
import org.lab.api.dto.milestone.MilestoneUpdateRequest;
import org.lab.api.dto.ticket.CreateTicketRequest;
import org.lab.api.dto.ticket.TicketAnswer;
import org.lab.data.entity.MilestoneStatus;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class MilestoneControllerTest extends AbstractAuthorizedTest {

    @Test
    public void testCreateAndUpdateMilestone() {
        Instant now = Instant.now();
        Instant start = now.plusSeconds(10);
        Instant end = now.plusSeconds(1000);

        MilestoneAnswer created = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new MilestoneCreateRequest(project.id(), "Initial Milestone", "desc", start, end))
                .when()
                .post("/api/milestone")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        assertThat(created.name()).isEqualTo("Initial Milestone");
        assertThat(created.description()).isEqualTo("desc");

        Instant newStart = now.plusSeconds(20);
        Instant newEnd = now.plusSeconds(2000);
        MilestoneAnswer updated = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new MilestoneUpdateRequest(created.id(), "Updated Milestone", "new desc", newStart, newEnd))
                .when()
                .put("/api/milestone")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo("Updated Milestone");
        assertThat(updated.description()).isEqualTo("new desc");
        assertThat(updated.status()).isEqualTo(created.status());
    }

    @Test
    public void testActivateGetListAndCloseMilestoneWorkflow() {
        Instant now = Instant.now();
        Instant start = now.plusSeconds(5);
        Instant end = now.plusSeconds(1000);

        MilestoneAnswer milestone = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new MilestoneCreateRequest(project.id(), "Workflow Milestone", "workflow desc", start, end))
                .when()
                .post("/api/milestone")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", project.id())
                .when()
                .get("/api/milestone/current")
                .then()
                .statusCode(404);

        MilestoneAnswer activated = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("milestoneId", milestone.id())
                .when()
                .post("/api/milestone/activate")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        assertThat(activated.id()).isEqualTo(milestone.id());
        assertThat(activated.status()).isEqualTo(MilestoneStatus.ACTIVE);

        MilestoneAnswer current = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", project.id())
                .when()
                .get("/api/milestone/current")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        assertThat(current.id()).isEqualTo(milestone.id());

        ListAnswer<MilestoneAnswer> list = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", project.id())
                .when()
                .get("/api/milestone")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});

        assertThat(list.getItemCount()).isGreaterThanOrEqualTo(1);
        assertThat(list.getItems()).contains(current);

        TicketAnswer ticket = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "Ticket for close", "t desc"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("milestoneId", milestone.id())
                .when()
                .post("/api/milestone/close")
                .then()
                .statusCode(500);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .post("/api/ticket/completed")
                .then()
                .statusCode(200);

        MilestoneAnswer closed = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("milestoneId", milestone.id())
                .when()
                .post("/api/milestone/close")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        assertThat(closed.id()).isEqualTo(milestone.id());
        assertThat(closed.status()).isEqualTo(MilestoneStatus.CLOSED);
    }
}
