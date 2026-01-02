package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.TicketDeveloper;

import java.util.Collection;

@RequiredArgsConstructor
public class GetTicketDevelopers implements AuthorizedDataCommand<Collection<TicketDeveloper>> {

    private final long ticketId;

    @Override
    public Collection<TicketDeveloper> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var ticket = databaseProvider.getTicketRepository().findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        var project = databaseProvider.getProjectRepository().findByTicketId(ticket.ticketId()).orElseThrow(() ->
                new IllegalArgumentException("Project not found")
        );
        databaseProvider.getProjectUserRepository().findProjectUser(project.projectId(), auth.userId()).orElseThrow(() ->
                new IllegalArgumentException("You don't have access to project")
        );
        return databaseProvider.getTicketRepository().getTicketDevelopers(ticket.ticketId());
    }

}
