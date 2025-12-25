package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Milestone;
import org.lab.data.entity.MilestoneStatus;

import java.util.Objects;

@RequiredArgsConstructor
public class CloseMilestoneCommand implements AuthorizedDataCommand<Milestone> {

    private final long milestoneId;

    @Override
    public Milestone execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var milestone = databaseProvider.getMilestoneRepository().findById(milestoneId).orElseThrow(() ->
                new IllegalArgumentException("Milestone not found")
        );
        var project = databaseProvider.getProjectRepository().findById(milestone.projectId()).orElseThrow(() ->
                new IllegalArgumentException("Project not found")
        );
        if (!Objects.equals(auth.userId(), project.teamLeadId()) && !Objects.equals(auth.userId(), project.projectManagerId())) {
            throw new IllegalStateException("User don't have permissions");
        }
        return switch (milestone.status()) {
            case CLOSED -> milestone;
            case OPEN -> throw new IllegalStateException("Milestone not acticated");
            case ACTIVE -> {
                //TODO: check tasks status
                databaseProvider.getMilestoneRepository().updateMilestone(
                        milestone.toBuilder().status(MilestoneStatus.CLOSED).build()
                );
                yield  databaseProvider.getMilestoneRepository().findById(milestone.milestoneId()).orElseThrow(() ->
                        new IllegalArgumentException("Milestone not found")
                );
            }
        };

    }
}
