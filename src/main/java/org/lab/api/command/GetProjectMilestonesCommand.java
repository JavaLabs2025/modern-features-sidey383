package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Milestone;

import java.util.Collection;

@RequiredArgsConstructor
public class GetProjectMilestonesCommand implements AuthorizedDataCommand<Collection<Milestone>> {

    private final long projectId;

    @Override
    public Collection<Milestone> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        return databaseProvider.getMilestoneRepository().findAllByProject(projectId);
    }
}
