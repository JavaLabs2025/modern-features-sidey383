package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.ProjectRole;
import org.lab.data.entity.ProjectUser;

@RequiredArgsConstructor
public final class SetupTeamLeadCommand implements AuthorizedDataCommand<Void> {

    private final long projectId;
    private final long user;

    @Override
    public Void execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var project = databaseProvider.getProjectRepository().findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("Project with id " + projectId + " does not exist")
        );
        if (project.projectManagerId() != auth.userId()) {
            throw new IllegalStateException("You not a project manager");
        }
        if (project.projectManagerId() == user) {
            throw new IllegalStateException("Project manager can't change position");
        }
        databaseProvider.getProjectUserRepository().removeTeamLead(projectId);
        databaseProvider.getProjectRepository().setTeamLead(projectId, user);
        databaseProvider.getProjectUserRepository().addUserToProject(new ProjectUser(projectId, user, ProjectRole.TEAM_LEADER, true));
        return null;
    }
}
