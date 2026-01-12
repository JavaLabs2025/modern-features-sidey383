package org.lab.api;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lab.AbstractAuthorizedTest;
import org.lab.api.dto.*;
import org.lab.api.dto.milestone.MilestoneAnswer;
import org.lab.api.dto.milestone.MilestoneCreateRequest;
import org.lab.api.dto.ticket.CreateTicketRequest;
import org.lab.api.dto.ticket.TicketAnswer;
import org.lab.api.dto.ticket.TicketAssignRequest;
import org.lab.data.entity.ProjectRole;
import org.lab.data.entity.TicketDeveloper;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectControllerTest extends AbstractAuthorizedTest {

    @Test
    public void testGetMyProjects() {
        ListAnswer<ProjectWithRoleAnswer> projects = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .when()
                .get("/api/me/projects")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});
        assertThat(projects.getItemCount()).isGreaterThan(0);
        ProjectWithRoleAnswer projectWithRole = projects.getItems().iterator().next();
        assertThat(projectWithRole.projectRole()).isEqualTo(ProjectRole.MANAGER);
        assertThat(projectWithRole.project().manager().id()).isEqualTo(manager.user().id());
    }

    @Test
    public void testCreateProject() {
        ProjectAnswer newProject = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateProjectRequest("Test Project"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        assertThat(newProject.name()).isEqualTo("Test Project");
        assertThat(newProject.manager().id()).isEqualTo(manager.user().id());
    }

    @Test
    public void testSetupTeamLead() {
        // Create a new project first
        ProjectAnswer newProject = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateProjectRequest("Project with Team Lead"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        // Set team lead
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), teamLead.user().id(), null))
                .when()
                .post("/api/project/teamlead")
                .then()
                .statusCode(200);

        // Verify team lead is set by checking project users
        ProjectUsersAnswer projectUsers = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", newProject.id())
                .when()
                .get("/api/project/users")
                .then()
                .statusCode(200)
                .extract().body().as(ProjectUsersAnswer.class);

        boolean teamLeadFound = projectUsers.users().stream()
                .anyMatch(user -> user.user().id() == teamLead.user().id() &&
                        user.role() == ProjectRole.TEAM_LEADER);
        assertThat(teamLeadFound).isTrue();
    }

    @Test
    public void testAddDeveloper() {
        // Create a new project first
        ProjectAnswer newProject = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateProjectRequest("Project with Developer"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        // Add developer
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), developer.user().id(), null))
                .when()
                .post("/api/project/developer")
                .then()
                .statusCode(200);

        // Verify developer is added
        ProjectUsersAnswer projectUsers = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", newProject.id())
                .when()
                .get("/api/project/users")
                .then()
                .statusCode(200)
                .extract().body().as(ProjectUsersAnswer.class);

        boolean developerFound = projectUsers.users().stream()
                .anyMatch(user -> user.user().id() == developer.user().id() &&
                        user.role() == ProjectRole.DEVELOPER);
        assertThat(developerFound).isTrue();
    }

    @Test
    public void testAddTester() {
        // Create a new project first
        ProjectAnswer newProject = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateProjectRequest("Project with Tester"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        // Add tester
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), tester.user().id(), null))
                .when()
                .post("/api/project/tester")
                .then()
                .statusCode(200);

        // Verify tester is added
        ProjectUsersAnswer projectUsers = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", newProject.id())
                .when()
                .get("/api/project/users")
                .then()
                .statusCode(200)
                .extract().body().as(ProjectUsersAnswer.class);

        boolean testerFound = projectUsers.users().stream()
                .anyMatch(user -> user.user().id() == tester.user().id() &&
                        user.role() == ProjectRole.TESTER);
        assertThat(testerFound).isTrue();
    }

    @Test
    public void testDeleteFromProject() {
        // Create a new project first
        ProjectAnswer newProject = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateProjectRequest("Project for Deletion Test"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        // Add a developer
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), developer.user().id(), null))
                .when()
                .post("/api/project/developer")
                .then()
                .statusCode(200);

        // Delete developer from project
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), developer.user().id(), null))
                .when()
                .delete("/api/project/user")
                .then()
                .statusCode(200);

        // Verify developer is removed
        ProjectUsersAnswer projectUsers = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", newProject.id())
                .when()
                .get("/api/project/users")
                .then()
                .statusCode(200)
                .extract().body().as(ProjectUsersAnswer.class);

        boolean developerFound = projectUsers.users().stream()
                .anyMatch(user -> user.user().id() == developer.user().id());
        assertThat(developerFound).isFalse();
    }

    @Test
    public void testGetProjectUsers() {
        // Get users for the default project created in setup
        ProjectUsersAnswer projectUsers = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("projectId", project.id())
                .when()
                .get("/api/project/users")
                .then()
                .statusCode(200)
                .extract().body().as(ProjectUsersAnswer.class);

        assertThat(projectUsers.project().id()).isEqualTo(project.id());
        assertThat(projectUsers.users()).hasSize(4); // manager, team lead, developer, tester

        // Verify all roles are present
        boolean hasManager = projectUsers.users().stream()
                .anyMatch(user -> user.role() == ProjectRole.MANAGER);
        boolean hasTeamLead = projectUsers.users().stream()
                .anyMatch(user -> user.role() == ProjectRole.TEAM_LEADER);
        boolean hasDeveloper = projectUsers.users().stream()
                .anyMatch(user -> user.role() == ProjectRole.DEVELOPER);
        boolean hasTester = projectUsers.users().stream()
                .anyMatch(user -> user.role() == ProjectRole.TESTER);

        assertThat(hasManager).isTrue();
        assertThat(hasTeamLead).isTrue();
        assertThat(hasDeveloper).isTrue();
        assertThat(hasTester).isTrue();
    }

    @Test
    public void testProjectWorkflowIntegration() {
        // 1. Create a project
        ProjectAnswer newProject = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateProjectRequest("Integration Test Project"))
                .when()
                .post("/api/project")
                .then()
                .statusCode(201)
                .extract().body().as(ProjectAnswer.class);

        // 2. Add team members
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), teamLead.user().id(), null))
                .when()
                .post("/api/project/teamlead")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), developer.user().id(), null))
                .when()
                .post("/api/project/developer")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new ProjectUserUpdateRequest(newProject.id(), tester.user().id(), null))
                .when()
                .post("/api/project/tester")
                .then()
                .statusCode(200);

        // 3. Create a milestone
        Instant now = Instant.now();
        Instant start = now.plusSeconds(10);
        Instant end = now.plusSeconds(1000);

        MilestoneAnswer milestone = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new MilestoneCreateRequest(newProject.id(), "Test Milestone", "Integration test milestone", start, end))
                .when()
                .post("/api/milestone")
                .then()
                .statusCode(200)
                .extract().body().as(MilestoneAnswer.class);

        // 4. Activate milestone
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("milestoneId", milestone.id())
                .when()
                .post("/api/milestone/activate")
                .then()
                .statusCode(200);

        // 5. Create a ticket
        TicketAnswer ticket = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new CreateTicketRequest(milestone.id(), "Test Ticket", "Integration test ticket"))
                .when()
                .post("/api/ticket")
                .then()
                .statusCode(200)
                .extract().body().as(TicketAnswer.class);

        // 6. Assign developer to ticket
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .body(new TicketAssignRequest(ticket.id(), developer.user().id()))
                .when()
                .post("/api/ticket/assign")
                .then()
                .statusCode(200);

        // 7. Verify ticket is assigned
        ListAnswer<TicketDeveloper> ticketDevelopers = given()
                .contentType(ContentType.JSON)
                .header("Authorization", manager.accessToken())
                .queryParam("ticketId", ticket.id())
                .when()
                .get("/api/ticket/developers")
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});

        assertThat(ticketDevelopers.getItemCount()).isEqualTo(1);
        TicketDeveloper assignedDeveloper = ticketDevelopers.getItems().iterator().next();
        assertThat(assignedDeveloper.userId()).isEqualTo(developer.user().id());
    }
}
