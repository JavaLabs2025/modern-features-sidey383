package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.ProjectRole;
import org.lab.data.entity.Ticket;
import org.lab.data.entity.TicketStatus;

@RequiredArgsConstructor
public class CreateTicketCommand implements AuthorizedDataCommand<Ticket> {

    private final long milestoneId;
    private final String name;
    private final String description;

    @Override
    public Ticket execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var milestone = databaseProvider.getMilestoneRepository().findById(milestoneId).orElseThrow(() ->
                new IllegalArgumentException("Milestone not found.")
        );
        var projectUser = databaseProvider.getProjectUserRepository().findProjectUser(milestone.projectId(), auth.userId()).orElseThrow(() ->
                new IllegalStateException("You not in project")
        );
        if (projectUser.projectRole() != ProjectRole.MANAGER && projectUser.projectRole() != ProjectRole.TEAM_LEADER) {
            throw new IllegalStateException("You can't create tickets");
        }
        Ticket t = new Ticket(0, milestoneId, name, description, TicketStatus.NEW);
        long ticketId = databaseProvider.getTicketRepository().createTicket(t);
        return databaseProvider.getTicketRepository().findById(ticketId).orElseThrow(() -> new IllegalStateException("Ticket not found"));
    }
}
