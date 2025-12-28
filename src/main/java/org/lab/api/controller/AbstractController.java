package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.RequiredArgsConstructor;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.Command;
import org.lab.serice.CommandExecutor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractController {

    private final CommandExecutor commandExecutor;
    private final AuthorizationProvider authProvider;

    protected CommandExecutor commandExecutor() {
        return commandExecutor;
    }

    protected AuthorizationProvider authProvider() {
        return authProvider;
    }

    protected Handler authWrap(Consumer<Context> handler) {
        return authProvider.wrap(handler);
    }

    public abstract void endpointSetup(Javalin javalin);

    public <RESULT> RESULT execute(Command<RESULT> command) {
        return commandExecutor.execute(command);
    }

    protected long extractLongParam(Context context, String name) {
        String value = context.queryParam(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Require %s query parameter".formatted(name));
        }
        return Long.parseLong(value);
    }

}
