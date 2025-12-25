package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Milestone;

import java.util.Optional;

@RequiredArgsConstructor
public class GetActiveMilestoneCommand implements AuthorizedDataCommand<Optional<Milestone>> {

    private final long projectId;

    @Override
    public Optional<Milestone> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        return databaseProvider.getMilestoneRepository().findActiveByProject(projectId);
    }
}
