package org.lab.api.command;

import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.error.InvalidRequestException;
import org.lab.data.DatabaseProvider;
import org.lab.serice.JwtService;

public final class IssueJwtToken implements AuthentificationCommand<String> {
    @Override
    public boolean requireAuthorization() {
        return true;
    }

    @Override
    public String execute(DatabaseProvider databaseProvider, JwtService jwtService, AuthorizationProvider authorizationProvider) {
        var auth = authorizationProvider.currentAuthorization().orElseThrow(() ->
                new InvalidRequestException("User no authorized")
        );
        return jwtService.createToken(auth);
    }
}
