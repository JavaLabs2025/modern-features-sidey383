package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.ProjectRole;
import org.lab.data.entity.Ticket;

@RequiredArgsConstructor
public class UpdateTicketCommand implements AuthorizedDataCommand<Ticket> {

    private final long ticketId;
    private final String name;
    private final String description;

    @Override
    public Ticket execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var ticket = databaseProvider.getTicketRepository().findById(ticketId).orElseThrow(() ->
                new IllegalArgumentException("Ticket not found.")
        );
        var milestone = databaseProvider.getMilestoneRepository().findById(ticket.milestoneId()).orElseThrow(() ->
                new IllegalArgumentException("Milestone not found.")
        );
        var projectUser = databaseProvider.getProjectUserRepository().findProjectUser(milestone.projectId(), auth.userId()).orElseThrow(() ->
                new IllegalStateException("You not in project")
        );
        if (projectUser.projectRole() != ProjectRole.MANAGER && projectUser.projectRole() != ProjectRole.TEAM_LEADER) {
            throw new IllegalStateException("You can't create tickets");
        }
        ticket = ticket.toBuilder().name(name).description(description).build();
        databaseProvider.getTicketRepository().updateTicket(ticket);
        return databaseProvider.getTicketRepository().findById(ticketId).orElseThrow(() ->
                new IllegalStateException("Ticket not found")
        );
    }
}
