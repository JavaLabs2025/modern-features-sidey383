package org.lab.api.command;

import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.error.InvalidRequestException;
import org.lab.serice.JwtService;

public final class IssueJwtToken implements AuthentificationCommand<String> {
    @Override
    public boolean requireAuthorization() {
        return true;
    }

    public String execute(AuthorizationProvider authorizationProvider, JwtService jwtService) {
        var auth = authorizationProvider.currentAuthorization().orElseThrow(() ->
                new InvalidRequestException("User no authorized")
        );
        return jwtService.createToken(auth);
    }

}
