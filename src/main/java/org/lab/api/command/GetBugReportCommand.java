package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.BugReport;

@RequiredArgsConstructor
public class GetBugReportCommand implements AuthorizedDataCommand<BugReport> {

    private final long bugId;

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
        return bugReport;
    }

}
