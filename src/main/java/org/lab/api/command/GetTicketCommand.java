package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Ticket;

@RequiredArgsConstructor
public class GetTicketCommand implements AuthorizedDataCommand<Ticket> {

    private final Long ticketId;

    @Override
    public Ticket execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        return databaseProvider.getTicketRepository().findById(ticketId).orElseThrow(() ->
                new IllegalArgumentException("Can't found ticket")
        );
    }
}