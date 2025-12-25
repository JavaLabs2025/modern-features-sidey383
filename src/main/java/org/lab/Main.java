import org.lab.api.RestApi;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.configuration.JwtProperty;
import org.lab.configuration.TokenProperty;
import org.lab.data.DatabaseProvider;
import org.lab.serice.CommandExecutor;
import org.lab.serice.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

Logger log = LoggerFactory.getLogger("MAIN");

void main() throws InterruptedException {
    log.info("Starting Application");
    var jwtService = new JwtService(
            new JwtProperty("cn5lLwe7rXKsI4CNQ9WAUTuWR6Tf6W/h/4tSj1d6qCQ="),
            new TokenProperty(Duration.ofMinutes(15)),
            Clock.systemUTC()
    );
    var authorizationProvider = new AuthorizationProvider(jwtService);
    try (var dataProvider = new DatabaseProvider()) {
        var commandExecutor = new CommandExecutor(jwtService, dataProvider, authorizationProvider);
        var api = new RestApi(commandExecutor, authorizationProvider);
        final CountDownLatch shutdownLatch = new CountDownLatch(1);
        api.registerShutdownHook(shutdownLatch::countDown);
        api.start();
        shutdownLatch.await();
        log.info("Application disabled");
    }
}
