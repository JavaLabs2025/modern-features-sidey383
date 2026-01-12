package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.BugReport;
import org.lab.data.entity.BugReportStatus;

@RequiredArgsConstructor
public class CreateBugReportCommand implements AuthorizedDataCommand<BugReport> {

    private final long projectId;
    private final String name;
    private final String description;

    @Override
    public BugReport execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var optUser = databaseProvider.getProjectUserRepository().findProjectUser(projectId, auth.userId());
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("Current user have no access to that project");
        }
        var bugId = databaseProvider.getBugReportRepository().createBugReport(
                BugReport.builder()
                        .projectId(projectId)
                        .name(name)
                        .description(description)
                        .status(BugReportStatus.NEW)
                        .build()
        );
        return databaseProvider.getBugReportRepository().findById(bugId).orElseThrow(() ->
                new IllegalStateException("Can't found created bug report")
        );
    }
}
