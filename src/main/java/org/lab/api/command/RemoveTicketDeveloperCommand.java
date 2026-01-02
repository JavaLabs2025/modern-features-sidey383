package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;

@RequiredArgsConstructor
public class RemoveTicketDeveloperCommand implements AuthorizedDataCommand<Void> {

    private final long ticketId;
    private final long userId;


    @Override
    public Void execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        var ticket = databaseProvider.getTicketRepository().findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        var project = databaseProvider.getProjectRepository().findByTicketId(ticket.ticketId()).orElseThrow(() ->
                new IllegalArgumentException("Project not found")
        );
        var projectUser = databaseProvider.getProjectUserRepository().findProjectUser(project.projectId(), auth.userId()).orElseThrow(() ->
                new IllegalArgumentException("You don't have access to project")
        );
        switch (projectUser.projectRole()) {
            case MANAGER, TEAM_LEADER -> databaseProvider.getTicketRepository().removeDeveloperFromTicket(ticketId, userId);
            case TESTER, DEVELOPER -> throw new IllegalStateException("You don't have access to this project");
        }
        return null;
    }
}
