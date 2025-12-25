package org.lab.api.command;

import org.lab.data.DatabaseProvider;
import org.lab.data.entity.ProjectUser;

import java.util.Collection;

public class GetProjectUsersCommand implements DataCommand<Collection<ProjectUser>> {

    private final long projectId;

    public GetProjectUsersCommand(long projectId) {
        this.projectId = projectId;
    }

    @Override
    public Collection<ProjectUser> execute(DatabaseProvider databaseProvider) {
        return databaseProvider.getProjectUserRepository().getProjectUsersByProject(projectId);
    }
}
