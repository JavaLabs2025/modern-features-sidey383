package org.lab.api.command;

import org.lab.data.DatabaseProvider;

public sealed interface GetCommand<RESULT> extends Command<RESULT> permits GetUserCommand {

    RESULT execute(DatabaseProvider databaseProvider);

}
