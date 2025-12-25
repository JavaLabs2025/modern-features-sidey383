package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.lab.api.authorization.Authorization;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.api.dto.AuthorizationRequest;
import org.lab.api.dto.RegisterRequest;
import org.lab.api.dto.SessionUpdateRequest;
import org.lab.api.mapper.UserMapper;
import org.lab.data.entity.UserType;
import org.lab.serice.CommandExecutor;
import org.mapstruct.factory.Mappers;

public class AuthentificationController extends AbstractController {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public AuthentificationController(CommandExecutor commandExecutor, AuthorizationProvider authProvider) {
        super(commandExecutor, authProvider);
    }

    @Override
    public void endpointSetup(Javalin javalin) {
        javalin
                .post("/api/register", authWrap(this::registerUser))
                .post("/api/auth", authWrap(this::authUser))
                .post("/api/auth/updateToken", authWrap(this::updateToken));
    }

    private void registerUser(Context context) {
        RegisterRequest regRequest = context.bodyAsClass(RegisterRequest.class);
        var user = execute(new CreateUserCommand(regRequest.username(), regRequest.password(), UserType.DEFAULT));
        var session = execute(new CreateSessionCommand(user, context.host(), context.userAgent()));
        var authorization = new Authorization(user);
        authProvider().authorize(authorization, () -> {
            var jwtToken = execute(new IssueJwtToken());
            context.json(userMapper.mapToAnswer(user, jwtToken, session.sessionId()));
        });
    }

    private void authUser(Context context) {
        AuthorizationRequest request = context.bodyAsClass(AuthorizationRequest.class);
        var user = execute(new LoginCommand(request.username(), request.password()));
        var session = execute(new CreateSessionCommand(user, context.host(), context.userAgent()));
        var authorization = new Authorization(user);
        authProvider().authorize(authorization, () -> {
            var jwtToken = commandExecutor().execute(new IssueJwtToken());
            context.json(userMapper.mapToAnswer(user, jwtToken, session.sessionId()));
        });
    }

    private void updateToken(Context context) {
        SessionUpdateRequest request = context.bodyAsClass(SessionUpdateRequest.class);
        var session = execute(new ProlongateSessionCommand(request.refreshToken()));
        var user = execute(new GetUserCommand(session.userId()).nullChecked());
        var authorization = new Authorization(user);
        authProvider().authorize(authorization, () -> {
            var jwtToken = commandExecutor().execute(new IssueJwtToken());
            context.json(userMapper.mapToAnswer(user, jwtToken, session.sessionId()));
        });
    }

}
