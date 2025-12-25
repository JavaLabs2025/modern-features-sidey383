package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.User;
import org.lab.serice.EncryptService;
import org.lab.serice.JwtService;

@RequiredArgsConstructor
public final class LoginCommand implements AuthentificationCommand<User> {

    private final String username;
    private final String password;

    @Override
    public boolean requireAuthorization() {
        return false;
    }

    @Override
    public User execute(DatabaseProvider databaseProvider, JwtService jwtService, AuthorizationProvider authorizationProvider) {
        var user = databaseProvider.getUserRepository().findByUsername(username).orElseThrow(() ->
                new IllegalStateException("User not found by name " + username)
        );
        if (!EncryptService.verifyPassword(password, user.password())) {
            throw new IllegalArgumentException("Wrong password");
        }
        return user;
    }
}
