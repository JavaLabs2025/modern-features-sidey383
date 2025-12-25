package org.lab.serice;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.Command;
import org.lab.api.command.AuthentificationCommand;
import org.lab.api.command.CreateSessionCommand;
import org.lab.api.command.CreateUserCommand;
import org.lab.api.command.IssueJwtToken;
import org.lab.data.DatabaseProvider;

@RequiredArgsConstructor
public class CommandExecutor {

    private final JwtService jwtService;
    private final DatabaseProvider databaseVerticle;
    private final AuthorizationProvider authorizationProvider;

    public <RESULT> RESULT execute(Command<RESULT> command) {
        if (command.requireAuthorization()) {
            authorizationProvider.currentAuthorization().orElseThrow(() ->
                    new RuntimeException("Authorization required")
            );
        }
        return switch (command) {
            case AuthentificationCommand<RESULT> authentificationCommand ->
                executeAuthentification(authentificationCommand);
        };
    }

    @SuppressWarnings("unchecked")
    private <RESULT> RESULT executeAuthentification(AuthentificationCommand<RESULT> command) {
        return (RESULT) switch (command) {
            case CreateUserCommand createUserCommand ->
                    createUserCommand.execute(databaseVerticle.getUserRepository());
            case CreateSessionCommand createSessionCommand ->
                    createSessionCommand.execute(databaseVerticle.getSessionRepository());
            case IssueJwtToken issueJwtToken ->
                issueJwtToken.execute(authorizationProvider, jwtService);
        };
    }

}
