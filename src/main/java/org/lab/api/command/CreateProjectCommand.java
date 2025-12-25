package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Project;
import org.lab.data.entity.ProjectRole;
import org.lab.data.entity.ProjectUser;

@RequiredArgsConstructor
public final class CreateProjectCommand implements AuthorizedDataCommand<Project> {

    private final String name;

    @Override
    public Project execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var projectId = databaseProvider.getProjectRepository().createProject(name, auth.userId());
        databaseProvider.getProjectUserRepository()
                .addUserToProject(new ProjectUser(projectId, auth.userId(), ProjectRole.MANAGER, true));
        return databaseProvider.getProjectRepository().findById(projectId).orElseThrow(() ->
                new IllegalStateException("Project didn't created")
        );
    }
}
