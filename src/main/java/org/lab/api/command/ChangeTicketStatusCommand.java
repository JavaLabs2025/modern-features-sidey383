package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.ProjectRole;
import org.lab.data.entity.Ticket;
import org.lab.data.entity.TicketStatus;

@RequiredArgsConstructor
public class ChangeTicketStatusCommand implements AuthorizedDataCommand<Ticket> {

    private final long ticketId;
    private final TicketStatus ticketStatus;

    @Override
    public Ticket execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var ticket = databaseProvider.getTicketRepository().findById(ticketId).orElseThrow(() ->
                new IllegalArgumentException("Ticket not found")
        );
        var auth = authorizationProvider.currentAuthorizationRequired();
        var milestone = databaseProvider.getMilestoneRepository().findById(ticket.milestoneId()).orElseThrow(() ->
                new IllegalStateException("Milestone not found")
        );
        var projectUser = databaseProvider.getProjectUserRepository().findProjectUser(milestone.projectId(), auth.userId()).orElseThrow(() ->
                new IllegalArgumentException("Project user not found")
        );
        if (projectUser.projectRole() == ProjectRole.TESTER) {
            throw new IllegalStateException("You can't change ticket status");
        }

        ticket = ticket.toBuilder().status(ticketStatus).build();

        databaseProvider.getTicketRepository().updateTicket(ticket);

        return databaseProvider.getTicketRepository().findById(ticket.ticketId()).orElseThrow(() ->
                new IllegalArgumentException("Ticket not found")
        );
    }
}
