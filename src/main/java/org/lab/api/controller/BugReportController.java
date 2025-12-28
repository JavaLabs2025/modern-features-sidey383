package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.bug.BugReportCreateRequest;
import org.lab.api.dto.bug.BugReportUpdateRequest;
import org.lab.api.mapper.BugReportMapper;
import org.lab.data.entity.BugReportStatus;
import org.lab.serice.CommandExecutor;
import org.mapstruct.factory.Mappers;

public class BugReportController extends AbstractController {

    private final BugReportMapper bugReportMapper = Mappers.getMapper(BugReportMapper.class);

    public BugReportController(CommandExecutor commandExecutor, AuthorizationProvider authProvider) {
        super(commandExecutor, authProvider);
    }

    @Override
    public void endpointSetup(Javalin javalin) {
        javalin
                .post("/api/bug-report", authWrap(this::createBugReport))
                .put("/api/bug-report",authWrap(this::updateBugReport) )
                .get("/api/bug-reports", authWrap(this::getBugReports))
                .get("/api/bug-report", authWrap(this::getBugReport))
                .post("/api/bug-report/fix", authWrap(this::fixBugReport))
                .post("/api/bug-report/tested", authWrap(this::testBugReport))
                .post("/api/bug-report/close", authWrap(this::closedBugReport));
    }

    public void createBugReport(Context context) {
        var request = context.bodyAsClass(BugReportCreateRequest.class);
        var bugReport = execute(new CreateBugReportCommand(request.projectId(), request.name(), request.description()));
        context.json(bugReportMapper.mapToAnswer(bugReport));
    }

    public void updateBugReport(Context context) {
        var request = context.bodyAsClass(BugReportUpdateRequest.class);
        var bugReport = execute(new UpdateBugReportCommand(request.bugReportId(), request.name(), request.description(), null));
        context.json(bugReportMapper.mapToAnswer(bugReport));
    }

    public void getBugReport(Context context) {
        var bugReport = execute(new GetBugReportCommand(getBugReportId(context)));
        context.json(bugReportMapper.mapToAnswer(bugReport));
    }

    public void getBugReports(Context context) {
         var bugReports = execute(new GetBugReportsCommand(extractLongParam(context, "projectId"))).stream()
                        .map(bugReportMapper::mapToAnswer)
                                .toList();
        context.json(new ListAnswer<>(bugReports));
    }

    public void fixBugReport(Context context) {
        var bugReport = execute(new ChangeBugReportStatusCommand(getBugReportId(context), BugReportStatus.FIXED));
        context.json(bugReportMapper.mapToAnswer(bugReport));
    }

    public void testBugReport(Context context) {
        var bugReport = execute(new ChangeBugReportStatusCommand(getBugReportId(context), BugReportStatus.TESTED));
        context.json(bugReportMapper.mapToAnswer(bugReport));
    }

    public void closedBugReport(Context context) {
        var bugReport = execute(new ChangeBugReportStatusCommand(getBugReportId(context), BugReportStatus.CLOSED));
        context.json(bugReportMapper.mapToAnswer(bugReport));
    }

    private long getBugReportId(Context context) {
        return extractLongParam(context, "bugReportId");
    }

}
