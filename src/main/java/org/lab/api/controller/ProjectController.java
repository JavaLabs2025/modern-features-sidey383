package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.val;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.api.dto.CreateProjectRequest;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.ProjectUserUpdateRequest;
import org.lab.api.mapper.ProjectMapper;
import org.lab.data.entity.Project;
import org.lab.data.entity.User;
import org.lab.serice.CommandExecutor;
import org.mapstruct.factory.Mappers;

import java.util.Objects;
import java.util.Optional;

public class ProjectController extends AbstractController {

    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    public ProjectController(CommandExecutor commandExecutor, AuthorizationProvider authProvider) {
        super(commandExecutor, authProvider);
    }

    @Override
    public void endpointSetup(Javalin javalin) {
        javalin
                .get("/api/me/projects", authWrap(this::getMeProjects))
                .post("/api/project", authWrap(this::createProject))
                .post("/api/project/teamlead", authWrap(this::changeTeamLead))
                .post("/api/project/developer", authWrap(this::addDeveloper))
                .post("/api/project/tester", authWrap(this::addTester))
                .delete("/api/project/user", authWrap(this::deleteFromProject))
                .get("/api/project/users", authWrap(this::getProjectUsers))
        ;
    }

    private void getMeProjects(Context context) {
        var projectUsers = execute(new GetMyProjectsCommand());
        var result = projectUsers.stream()
                .map(projectUser -> {
                    var project = execute(new GetProjectCommand(projectUser.projectId()).nullChecked());
                    var manager = projectManager(project);
                    var teamLeadOpt = teamLead(project);
                    return projectMapper.mapToAnswer(projectUser, project, manager, teamLeadOpt.orElse(null));
                })
                .toList();
        context.json(new ListAnswer<>(result));
    }

    private void createProject(Context context) {
        var request = context.bodyAsClass(CreateProjectRequest.class);
        var project = execute(new CreateProjectCommand(request.name()));
        var manager = projectManager(project);
        var teamLeadOpt = teamLead(project);
        context.json(projectMapper.mapToAnswer(project, manager, teamLeadOpt.orElse(null)));
        context.status(HttpStatus.CREATED);
    }

    private void changeTeamLead(Context context) {
        var request = context.bodyAsClass(ProjectUserUpdateRequest.class);
        var user = execute(new GetUserCommand(request.userId(), request.username()).nullChecked());
        execute(new SetupTeamLeadCommand(request.projectId(), user.userId()));
    }

    private void addDeveloper(Context context) {
        var request = context.bodyAsClass(ProjectUserUpdateRequest.class);
        var user = execute(new GetUserCommand(request.userId(), request.username()).nullChecked());
        execute(new AddDeveloperCommand(request.projectId(), user.userId()));
    }

    private void addTester(Context context) {
        var request = context.bodyAsClass(ProjectUserUpdateRequest.class);
        var user = execute(new GetUserCommand(request.userId(), request.username()).nullChecked());
        execute(new AddTesterCommand(request.projectId(), user.userId()));
    }

    private void deleteFromProject(Context context) {
        var request = context.bodyAsClass(ProjectUserUpdateRequest.class);
        var user = execute(new GetUserCommand(request.userId(), request.username()).nullChecked());
        execute(new DeleteProjectUserCommand(request.projectId(), user.userId()));
    }

    private void getProjectUsers(Context context) {
        long projectId = Long.parseLong(Objects.requireNonNull(context.queryParam("projectId")));
        var projectUsers = execute(new GetProjectUsersCommand(projectId));
        val project = execute(new GetProjectCommand(projectId).nullChecked());
        var usersAnswer = projectUsers.stream()
                .map(pu -> {
                    var user = execute(new GetUserCommand(pu.userId()).nullChecked());
                    return projectMapper.mapToAnswer(user, pu);
                })
                .toList();
        var manager = projectManager(project);
        var teamLeadOpt = teamLead(project);
        context.json(projectMapper.mapToAnswer(project, manager, teamLeadOpt.orElse(null), usersAnswer));
    }

    private User projectManager(Project project) {
        return execute(new GetUserCommand(project.projectManagerId()).nullChecked());
    }

    private Optional<User> teamLead(Project project) {
        return project.getTeamLead()
                .map(teamLeadId -> execute(new GetUserCommand(teamLeadId).nullChecked()));
    }

}
