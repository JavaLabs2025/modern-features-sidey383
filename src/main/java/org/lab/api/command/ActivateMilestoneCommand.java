package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Milestone;
import org.lab.data.entity.MilestoneStatus;

import java.util.Objects;

@RequiredArgsConstructor
public class ActivateMilestoneCommand implements AuthorizedDataCommand<Milestone> {

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
            case ACTIVE -> milestone;
            case CLOSED -> throw new IllegalStateException("Milestone already closed");
            case OPEN -> {
                databaseProvider.getMilestoneRepository().findActiveByProject(milestone.projectId()).ifPresent(_ -> {
                    throw new IllegalStateException("Already have active milestone");
                });
                databaseProvider.getMilestoneRepository().updateMilestone(
                        milestone.toBuilder().status(MilestoneStatus.ACTIVE).build()
                );
                yield  databaseProvider.getMilestoneRepository().findById(milestone.milestoneId()).orElseThrow(() ->
                        new IllegalArgumentException("Milestone not found")
                );
            }
        };

    }
}
