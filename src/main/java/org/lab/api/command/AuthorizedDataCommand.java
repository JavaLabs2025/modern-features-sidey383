package org.lab.api.command;

import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;

public non-sealed interface AuthorizedDataCommand<RESULT> extends Command<RESULT> {

    RESULT execute(DatabaseProvider databaseProvider, AuthorizationProvider authorizationProvider);

}
