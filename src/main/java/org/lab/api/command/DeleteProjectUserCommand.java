package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;

import java.util.Objects;

@RequiredArgsConstructor
public final class DeleteProjectUserCommand implements AuthorizedDataCommand<Void> {

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
            throw new IllegalStateException("Can't remove project manager");
        }
        if (Objects.equals(project.teamLeadId(), user)) {
            databaseProvider.getProjectUserRepository().removeTeamLead(projectId);
            databaseProvider.getProjectRepository().setTeamLead(projectId, null);
        }
        databaseProvider.getProjectUserRepository().remove(projectId, user);
        return null;
    }
}
