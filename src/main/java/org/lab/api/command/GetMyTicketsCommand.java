package org.lab.api.command;

import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Ticket;

import java.util.Collection;

public class GetMyTicketsCommand implements AuthorizedDataCommand<Collection<Ticket>> {

    @Override
    public Collection<Ticket> execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorizationRequired();
        return databaseProvider.getTicketRepository().findTicketsByDeveloper(auth.userId());
    }
}
