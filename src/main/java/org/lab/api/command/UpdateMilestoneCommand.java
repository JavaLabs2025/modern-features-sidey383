package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Milestone;
import org.lab.data.entity.MilestoneStatus;

import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
public class UpdateMilestoneCommand implements AuthorizedDataCommand<Milestone> {

    private final long milestoneId;
    private final String name;
    private final String description;
    private final Instant startTime;
    private final Instant endTime;

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
        databaseProvider.getMilestoneRepository().updateMilestone(
                milestone.toBuilder()
                        .name(name)
                        .description(description)
                        .startTime(startTime)
                        .endTime(endTime)
                        .status(MilestoneStatus.OPEN)
                        .active(true)
                        .build()
        );
        return databaseProvider.getMilestoneRepository().findById(milestone.milestoneId()).orElseThrow(() ->
                new IllegalArgumentException("Milestone not found")
        );
    }
}
