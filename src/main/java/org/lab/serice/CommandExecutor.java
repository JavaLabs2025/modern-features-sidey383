package org.lab.serice;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.data.DatabaseProvider;

@RequiredArgsConstructor
public class CommandExecutor {

    private final JwtService jwtService;
    private final DatabaseProvider databaseProvider;
    private final AuthorizationProvider authorizationProvider;

    public <RESULT> RESULT execute(Command<RESULT> command) {

        return switch (command) {
            case AuthentificationCommand<RESULT> authentificationCommand -> {
                if (authentificationCommand.requireAuthorization()) {
                    checkAuthorization();
                }
                yield authentificationCommand.execute(databaseProvider, jwtService, authorizationProvider);
            }
            case DataCommand<RESULT> dataCommand -> dataCommand.execute(databaseProvider);
            case AuthorizedDataCommand<RESULT> authorizedDataCommand -> {
                checkAuthorization();
                yield authorizedDataCommand.execute(databaseProvider, authorizationProvider);
            }
        };
    }

    private void checkAuthorization() {
        authorizationProvider.currentAuthorization().orElseThrow(() ->
                new RuntimeException("Authorization required")
        );
    }

}
