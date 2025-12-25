package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.milestone.MilestoneCreateRequest;
import org.lab.api.dto.milestone.MilestoneUpdateRequest;
import org.lab.api.mapper.MilestoneMapper;
import org.lab.serice.CommandExecutor;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

public class MilestoneController extends AbstractController {

    private final MilestoneMapper milestoneMapper = Mappers.getMapper(MilestoneMapper.class);

    public MilestoneController(CommandExecutor commandExecutor, AuthorizationProvider authProvider) {
        super(commandExecutor, authProvider);
    }

    @Override
    public void endpointSetup(Javalin javalin) {
        javalin
                .post("/api/milestone", authWrap(this::createMilestone))
                .put("/api/milestone", authWrap(this::updateMilestone))
                .get("/api/milestone", authWrap(this::getMilestones))
                .get("/api/milestones", authWrap(this::getMilestones))
                .post("/api/milestone/activate", authWrap(this::activateMilestone))
                .post("/api/milestone/close", authWrap(this::closeMilestone))
                .get("/api/milestone/current", authWrap(this::currentMilestone));
    }

    public void createMilestone(Context context) {
        var request = context.bodyAsClass(MilestoneCreateRequest.class);
        var milestone = execute(new CreateMilestoneCommand(
                request.projectId(),
                request.name(),
                request.description(),
                request.startTime(),
                request.endTime())
        );
        context.json(milestoneMapper.toAnswer(milestone));
    }

    public void updateMilestone(Context context) {
        var request = context.bodyAsClass(MilestoneUpdateRequest.class);
        var milestone = execute(new UpdateMilestoneCommand(
                request.milestoneId(),
                request.name(),
                request.description(),
                request.startTime(),
                request.endTime()
        ));
        context.json(milestoneMapper.toAnswer(milestone));
    }

    public void currentMilestone(Context context) {
        long projectId = Long.parseLong(Objects.requireNonNull(context.queryParam("projectId")));
        execute(new GetActiveMilestoneCommand(projectId)).ifPresentOrElse(
                milestone -> context.json(milestoneMapper.toAnswer(milestone)),
                () -> context.status(HttpStatus.NOT_FOUND)
        );
    }

    public void getMilestones(Context context) {
        long projectId = Long.parseLong(Objects.requireNonNull(context.queryParam("projectId")));
        var milestones = execute(new GetProjectMilestonesCommand(projectId)).stream()
                .map(milestoneMapper::toAnswer)
                .toList();
        context.json(new ListAnswer<>(milestones));
    }

    public void activateMilestone(Context context) {
        long milestoneId = Long.parseLong(Objects.requireNonNull(context.queryParam("milestoneId")));
        var milestone = execute(new ActivateMilestoneCommand(milestoneId));
        context.json(milestoneMapper.toAnswer(milestone));
    }

    public void closeMilestone(Context context) {
        long milestoneId = Long.parseLong(Objects.requireNonNull(context.queryParam("milestoneId")));
        var milestone = execute(new CloseMilestoneCommand(milestoneId));
        context.json(milestoneMapper.toAnswer(milestone));
    }


}
