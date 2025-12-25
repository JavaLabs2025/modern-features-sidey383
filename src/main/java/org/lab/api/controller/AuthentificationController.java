package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.Authorization;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.api.dto.*;
import org.lab.data.entity.UserType;
import org.lab.serice.CommandExecutor;

@RequiredArgsConstructor
public class AuthentificationController {

    private final CommandExecutor commandExecutor;
    private final AuthorizationProvider authProvider;

    public Javalin setupMethods(Javalin javalin) {
        return javalin
                .post("/api/register", authProvider.wrap(this::registerUser))
                .post("/api/auth", authProvider.wrap(this::authUser))
                .post("/api/auth/updateToken", authProvider.wrap(this::updateToken));
    }

    private void registerUser(Context context) {
        RegisterRequest regRequest = context.bodyAsClass(RegisterRequest.class);
        var user = commandExecutor.execute(new CreateUserCommand(regRequest.username(), regRequest.password(), UserType.DEFAULT));
        var session = commandExecutor.execute(new CreateSessionCommand(user, context.host(), context.userAgent()));
        var authorization = new Authorization(user);
        authProvider.authorize(authorization, () -> {
            var jwtToken = commandExecutor.execute(new IssueJwtToken());
            context.json(new AuthorizationAnswer(
                    new UserAnswer(
                            user.username(),
                            user.userType()
                    ),
                    jwtToken,
                    session.sessionId()
            ));
        });
    }

    private void authUser(Context context) {
        AuthorizationRequest request = context.bodyAsClass(AuthorizationRequest.class);
        var user = commandExecutor.execute(new LoginCommand(request.username(), request.password()));
        var session = commandExecutor.execute(new CreateSessionCommand(user, context.host(), context.userAgent()));
        var authorization = new Authorization(user);
        authProvider.authorize(authorization, () -> {
            var jwtToken = commandExecutor.execute(new IssueJwtToken());
            context.json(new AuthorizationAnswer(
                    new UserAnswer(
                            user.username(),
                            user.userType()
                    ),
                    jwtToken,
                    session.sessionId()
            ));
        });
    }

    private void updateToken(Context context) {
        SessionUpdateRequest request = context.bodyAsClass(SessionUpdateRequest.class);
        var session = commandExecutor.execute(new ProlongateSessionCommand(request.refreshToken()));
        var user = commandExecutor.execute(new GetUserCommand(session.userId()));
        var authorization = new Authorization(user);
        authProvider.authorize(authorization, () -> {
            var jwtToken = commandExecutor.execute(new IssueJwtToken());
            context.json(new AuthorizationAnswer(
                    new UserAnswer(
                            user.username(),
                            user.userType()
                    ),
                    jwtToken,
                    session.sessionId()
            ));
        });
    }

}
