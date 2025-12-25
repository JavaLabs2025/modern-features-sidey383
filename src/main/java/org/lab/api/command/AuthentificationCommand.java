package org.lab.api.command;

import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.serice.JwtService;

public sealed interface AuthentificationCommand<RESULT> extends Command<RESULT> permits CreateSessionCommand, CreateUserCommand, IssueJwtToken, LoginCommand, ProlongateSessionCommand {

    boolean requireAuthorization();

    RESULT execute(DatabaseProvider databaseProvider, JwtService jwtService, AuthorizationProvider authorizationProvider);

}
