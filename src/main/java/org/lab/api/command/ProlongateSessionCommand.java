package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Session;
import org.lab.serice.JwtService;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public final class ProlongateSessionCommand implements AuthentificationCommand<Session> {

    private final String sessionId;

    @Override
    public boolean requireAuthorization() {
        return false;
    }

    @Override
    public Session execute(DatabaseProvider databaseProvider, JwtService jwtService, AuthorizationProvider authorizationProvider) {
        databaseProvider.getSessionRepository().prolongateSession(sessionId, Instant.now().plus(Duration.ofHours(16)));
        return databaseProvider.getSessionRepository().getSession(sessionId).orElseThrow(() ->
                new IllegalArgumentException("Session not found")
        );
    }

}
