package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.BugReport;
import org.lab.data.entity.BugReportStatus;

@RequiredArgsConstructor
public class UpdateBugReportCommand implements AuthorizedDataCommand<BugReport> {

    private final long bugId;
    private final String name;
    private final String description;
    private final BugReportStatus status;

    @Override
    public BugReport execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var bugReport = databaseProvider.getBugReportRepository().findById(bugId).orElseThrow(() ->
                new IllegalArgumentException("Can't found bug report")
                );
        var optUser = databaseProvider.getProjectUserRepository().findProjectUser(bugReport.projectId(), auth.userId());
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("Current user have no access to that project");
        }
        var bugId = databaseProvider.getBugReportRepository().createBugReport(
                BugReport.builder()
                        .name(name == null ? bugReport.name() :  name)
                        .description(description == null ? bugReport.description() : description)
                        .status(status == null ? bugReport.status() : status)
                        .build()
        );
        return databaseProvider.getBugReportRepository().findById(bugId).orElseThrow(() ->
                new IllegalStateException("Can't found created bug report")
        );
    }
}
