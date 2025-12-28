package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.BugReport;
import org.lab.data.entity.BugReportStatus;

import java.util.Set;

@RequiredArgsConstructor
public class ChangeBugReportStatusCommand implements AuthorizedDataCommand<BugReport> {

    private static final Set<BugReportStatus> ALLOW_TO_TESTER = Set.of(BugReportStatus.NEW, BugReportStatus.TESTED);
    private static final Set<BugReportStatus> ALLOW_TO_DEVELOPER = Set.of(BugReportStatus.NEW, BugReportStatus.FIXED);

    private final long bugReportId;
    private final BugReportStatus bugReportStatus;

    @Override
    public BugReport execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {

        var authorization = authorizationProvider.currentAuthorizationRequired();

        var bugReport = databaseProvider.getBugReportRepository().findById(bugReportId).orElseThrow(() ->
                new IllegalArgumentException("Bug report with id " + bugReportId + " does not exist")
        );
        long projectId = bugReport.projectId();
        var project = databaseProvider.getProjectRepository().findById(projectId).orElseThrow(() ->
                new IllegalStateException("Project with id " + projectId + " does not exist, but bug report " + bugReportId + " reference on them")
        );
        var projectUser = databaseProvider.getProjectUserRepository().findProjectUser(project.projectId(), authorization.userId()).orElseThrow(() ->
                new IllegalArgumentException("User " + authorization.username() + " didn't belong to porject " + project.projectId())
        );

        switch (projectUser.projectRole()) {
            case TESTER -> {
                if (!ALLOW_TO_TESTER.contains(bugReportStatus)) {
                    throw new IllegalArgumentException("Bug report status " + bugReportStatus + " not allowed for tester");
                }
            }
            case DEVELOPER -> {
                if (!ALLOW_TO_DEVELOPER.contains(bugReportStatus)) {
                    throw new IllegalArgumentException("Bug report status " + bugReportStatus + " not allowed for developer");
                }
            }
        }
        switch (projectUser.projectRole()) {
            case TESTER, DEVELOPER -> {
                if (bugReportStatus.isBefore(bugReport.status())) {
                    throw new IllegalStateException("You can't change bug report status from " + bugReport.status() + " to " + bugReportStatus);
                }
            }
        }
        bugReport = bugReport.toBuilder().status(bugReportStatus).build();

        databaseProvider.getBugReportRepository().updateBugReport(bugReport);
        return bugReport;
    }
}
