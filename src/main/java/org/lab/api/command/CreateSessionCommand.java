package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Session;
import org.lab.data.entity.User;
import org.lab.data.repository.SessionRepository;
import org.lab.serice.JwtService;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;

@RequiredArgsConstructor
public final class CreateSessionCommand implements AuthentificationCommand<Session> {

    private static final Random random = new SecureRandom();

    private final User user;
    private final String ipAddress;
    private final String userAgent;

    @Override
    public boolean requireAuthorization() {
        return false;
    }

    public Session execute(SessionRepository sessionRepository) {
        return sessionRepository.createSession(user.userId(), generateSessionId(), ipAddress, userAgent, Instant.now().plus(Duration.ofHours(16)));
    }

    private String generateSessionId() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public Session execute(DatabaseProvider databaseProvider, JwtService jwtService, AuthorizationProvider authorizationProvider) {
        return databaseProvider.getSessionRepository()
                .createSession(user.userId(), generateSessionId(), ipAddress, userAgent, Instant.now().plus(Duration.ofHours(16)));
    }
}
