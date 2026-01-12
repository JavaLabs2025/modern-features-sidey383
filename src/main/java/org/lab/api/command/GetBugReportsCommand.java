package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.BugReport;

import java.util.Collection;

@RequiredArgsConstructor
public class GetBugReportsCommand implements AuthorizedDataCommand<Collection<BugReport>> {

    private final long projectId;

    @Override
    public Collection<BugReport> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var optUser = databaseProvider.getProjectUserRepository().findProjectUser(projectId, auth.userId());
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("You have no access to project");
        }
        return databaseProvider.getBugReportRepository().findByProjectId(projectId);
    }

}
