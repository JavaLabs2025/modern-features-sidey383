package org.lab.api.command;

import org.lab.data.DatabaseProvider;

public non-sealed interface DataCommand<RESULT> extends Command<RESULT> {

    RESULT execute(DatabaseProvider databaseProvider);

}
