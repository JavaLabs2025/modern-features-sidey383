package org.lab.api;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.lab.AbstractAuthorizedTest;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.milestone.MilestoneAnswer;
import org.lab.api.dto.milestone.MilestoneCreateRequest;
import org.lab.api.dto.ticket.CreateTicketRequest;
import org.lab.api.dto.ticket.TicketAnswer;
import org.lab.api.dto.ticket.TicketAssignRequest;
import org.lab.api.dto.ticket.UpdateTicketRequest;
import org.lab.data.entity.TicketDeveloper;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class TicketControllerTest extends AbstractAuthorizedTest {

    protected static MilestoneAnswer milestone;

    @Order(2)
    @BeforeAll
    public static void setupMilestone() {
        Instant now = Instant.now();
        var start = now.plusSeconds(10);
        var end = now.plusSeconds(10000);

        milestone = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new MilestoneCreateRequest(project.id(), "Sample milestone", "desc", start, end))
                .when()
                .post("/api/milestone")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("milestoneId", milestone.id())
                .when()
                .post("/api/milestone/activate")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateTicketByManagerAndTeamLead() {
        TicketAnswer managerTicket = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "Manager Ticket", "Created by manager"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        assertThat(managerTicket.id()).isGreaterThan(0);
        assertThat(managerTicket.name()).isEqualTo("Manager Ticket");

        TicketAnswer tlTicket = given()
                .contentType(ContentType.JSON)
                .header("Authorization", teamLead.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "TL Ticket", "Created by team lead"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        assertThat(tlTicket.id()).isGreaterThan(0);
        assertThat(tlTicket.name()).isEqualTo("TL Ticket");
    }

    @Test
    public void testUpdateAndGetTicket() {
        TicketAnswer created = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "Original", "Original desc"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        TicketAnswer updated = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new UpdateTicketRequest(created.id(), "Updated", "Updated desc"))
                .when()
                .put("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo("Updated");
        assertThat(updated.description()).isEqualTo("Updated desc");

        TicketAnswer fetched = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("ticketId", created.id())
                .when()
                .get("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.name()).isEqualTo(updated.name());
    }

    @Test
    public void testAssignAndDeassignDeveloperAndGetDevelopers() {
        TicketAnswer ticket = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "AssignTicket", "to assign"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new TicketAssignRequest(ticket.id(), developer.user().id()))
                .when()
                .post("/api/ticket/assign")
                .then()
                .statusCode(200);

        ListAnswer<TicketDeveloper> devs = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .get("/api/ticket/developers")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});

        assertThat(devs.getItemCount()).isEqualTo(1);
        TicketDeveloper assigned = devs.getItems().iterator().next();
        assertThat(assigned.userId()).isEqualTo(developer.user().id());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new TicketAssignRequest(ticket.id(), developer.user().id()))
                .when()
                .post("/api/ticket/deassign")
                .then()
                .statusCode(200);

        ListAnswer<TicketDeveloper> devsAfter = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .get("/api/ticket/developers")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});

        assertThat(devsAfter.getItemCount()).isEqualTo(0);
    }

    @Test
    public void testTicketStatusWorkflowAndGetMyTickets() {
        TicketAnswer ticket = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "WorkflowTicket", "status flow"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new TicketAssignRequest(ticket.id(), developer.user().id()))
                .when()
                .post("/api/ticket/assign")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .post("/api/ticket/accepted")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .post("/api/ticket/in-progress")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .post("/api/ticket/completed")
                .then()
                .statusCode(200);

        ListAnswer<TicketAnswer> myTickets = given()
                .contentType(ContentType.JSON)
                .header("Authorization", developer.accessToken())
                .when()
                .get("/api/me/tickets")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});

        assertThat(myTickets.getItemCount()).isGreaterThanOrEqualTo(1);
        List<TicketAnswer> items = List.copyOf(myTickets.getItems());
        boolean found = items.stream().anyMatch(t -> t.id() == ticket.id());
        assertThat(found).isTrue();
    }

    @Test
    public void testGetTicketsByMilestone() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "ListTicket1", "one"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "ListTicket2", "two"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200);

        ListAnswer<TicketAnswer> list = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("milestoneId", milestone.id())
                .when()
                .get("/api/tickets")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});

        assertThat(list.getItemCount()).isGreaterThanOrEqualTo(2);
    }
}
