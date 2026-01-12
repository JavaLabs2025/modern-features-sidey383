package org.lab.api;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.SecurityComponentConfiguration;
import io.javalin.openapi.plugin.redoc.ReDocConfiguration;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import lombok.extern.slf4j.Slf4j;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.controller.*;
import org.lab.serice.CommandExecutor;

import java.util.Collection;
import java.util.List;

@Slf4j
public class RestApi {

    private final Javalin javalin;

    public RestApi(CommandExecutor commandExecutor, AuthorizationProvider authorizationProvider) {
        Collection<AbstractController> controllers = List.of(
                new AuthentificationController(commandExecutor, authorizationProvider),
                new ProjectController(commandExecutor, authorizationProvider),
                new MilestoneController(commandExecutor, authorizationProvider),
                new BugReportController(commandExecutor, authorizationProvider),
                new TicketController(commandExecutor, authorizationProvider)
        );
        var javalinSetup = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(this::setupOpenApi));
            config.registerPlugin(new SwaggerPlugin(this::setupSwagger));
            config.registerPlugin(new ReDocPlugin(this::setupRedoc));
        });
        javalinSetup.get("/", ctx -> ctx.redirect("/swagger"));

        controllers.forEach(controller -> controller.endpointSetup(javalinSetup));

        this.javalin = javalinSetup;
    }

    public void start() {
        javalin.start(8080);
    }

    public void registerShutdownHook(Runnable onShutdown) {
        javalin.events(eventConfig ->
                eventConfig.serverStopped(onShutdown::run)
        );
    }

    private void setupOpenApi(OpenApiPluginConfiguration pluginConfig) {
        pluginConfig.withDocumentationPath("/openapi");
        pluginConfig.withPrettyOutput();
        pluginConfig.withDefinitionConfiguration((version, definition) -> definition
                .withInfo(info -> info
                        .description("Project control backend")
                )
                .withServer(openApiServer -> openApiServer
                        .description("Server description goes here")
                        .url("http://localhost:{port}{basePath}/" + version + "/")
                        .variable("port", "Server's port", "8080", "8080")
                        .variable("basePath", "Base path of the server", "", "", "v1")
                )
                .withSecurity(SecurityComponentConfiguration::withBearerAuth)
        );
    }

    private void setupSwagger(SwaggerConfiguration configuration) {
        configuration.setTitle("Project control");
        configuration.setDocumentationPath("/openapi");
    }

    private void setupRedoc(ReDocConfiguration configuration) {
        configuration.setDocumentationPath("/openapi");
    }

    public void stop() {
        javalin.stop();
    }

}
