package org.lab.api.command;

import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.ProjectUser;

import java.util.Collection;

public final class GetMyProjectsCommand implements AuthorizedDataCommand<Collection<ProjectUser>> {

    @Override
    public Collection<ProjectUser> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var authorization = authorizationProvider.currentAuthorization().orElseThrow(() ->
                new IllegalStateException("No authorization provided")
        );

        return databaseProvider.getProjectUserRepository().getProjectUsersByUser(authorization.userId());
    }
}
