package org.lab.api.authorization;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.lab.serice.JwtService;

import java.util.Optional;
import java.util.function.Consumer;
import java.lang.ScopedValue;

@RequiredArgsConstructor
public class AuthorizationProvider {

    private final ScopedValue<Optional<Authorization>> currentAuthorization = ScopedValue.newInstance();
    private final JwtService jwtService;

    public void authorize(Authorization authorization, Runnable authorizedOp) {
        ScopedValue.where(currentAuthorization, Optional.ofNullable(authorization)).run(authorizedOp);
    }

    public Optional<Authorization> currentAuthorization() {
        return currentAuthorization.get();
    }

    public Handler wrap(Consumer<@NonNull Context> handler) {
        return context -> {
            var authHeader = context.header("Authorization");
            if (authHeader == null) {
                authorize(null, () -> handler.accept(context));
            } else {
                authorize(
                        jwtService.getAuthorization(authHeader),
                        () -> handler.accept(context)
                );
            }
        };
    }

}
