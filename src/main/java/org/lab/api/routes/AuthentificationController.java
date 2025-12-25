package org.lab.api.routes;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.Authorization;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.CreateSessionCommand;
import org.lab.api.command.CreateUserCommand;
import org.lab.api.command.IssueJwtToken;
import org.lab.api.dto.AuthorizationAnswer;
import org.lab.api.dto.RegisterRequest;
import org.lab.api.dto.UserAnswer;
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
                .post("/api/auth/prolongate-session", authProvider.wrap(this::prolongateSession));
    }

    @OpenApi(
            path = "/api/register",
            requestBody = @OpenApiRequestBody(
                    required = true,
                    content = @OpenApiContent(from = RegisterRequest.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            content = @OpenApiContent(from = UserAnswer.class)
                    )
            }
    )
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

    }

    private void prolongateSession(Context context) {

    }

}
