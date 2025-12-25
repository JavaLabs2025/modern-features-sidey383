package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Milestone;
import org.lab.data.entity.MilestoneStatus;

import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
public class CreateMilestoneCommand implements AuthorizedDataCommand<Milestone> {

    private final long projectId;
    private final String name;
    private final String description;
    private final Instant startTime;
    private final Instant endTime;

    @Override
    public Milestone execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var project = databaseProvider.getProjectRepository().findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("Project not found")
        );
        var auth = authorizationProvider.currentAuthorizationRequired();
        if (!Objects.equals(auth.userId(), project.teamLeadId()) && !Objects.equals(auth.userId(), project.projectManagerId())) {
            throw new IllegalStateException("User don't have permissions");
        }
        var milestoneId = databaseProvider.getMilestoneRepository().createMilestone(
                new Milestone(0, projectId, name, description, startTime, endTime, MilestoneStatus.OPEN, true)
        );
        return databaseProvider.getMilestoneRepository().findById(milestoneId).orElseThrow(() ->
                new IllegalArgumentException("Milestone not found")
        );
    }
}
