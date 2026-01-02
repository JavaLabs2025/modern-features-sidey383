package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Ticket;

import java.util.Collection;

@RequiredArgsConstructor
public class GetTicketsCommand implements AuthorizedDataCommand<Collection<Ticket>> {

    private final Long milestoneId;

    @Override
    public Collection<Ticket> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        return databaseProvider.getTicketRepository().findTicketsByMilestone(milestoneId);
    }
}
