package org.lab;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.lab.api.RestApi;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.configuration.JwtProperty;
import org.lab.configuration.TokenProperty;
import org.lab.data.DatabaseProvider;
import org.lab.mock.ClockMock;
import org.lab.serice.CommandExecutor;
import org.lab.serice.JwtService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Testcontainers
public class AbstractTest {

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>(DockerImageName.parse("postgres:12.1-alpine"))
                    .withDatabaseName("features")
                    .withExposedPorts(5432)
                    .withUsername("postgres")
                    .withPassword("12345");

    protected static final ClockMock clock = new ClockMock();

    protected static final JwtService jwtService = new JwtService(
            new JwtProperty("cn5lLwe7rXKsI4CNQ9WAUTuWR6Tf6W/h/4tSj1d6qCQ="),
            new TokenProperty(Duration.ofMinutes(15)),
            clock
    );

    protected final AuthorizationProvider authorizationProvider = new AuthorizationProvider(jwtService);

    protected final DatabaseProvider dataProvider = new DatabaseProvider(database.getJdbcUrl(), database.getUsername(), database.getPassword());

    private final CommandExecutor commandExecutor = new CommandExecutor(jwtService, dataProvider, authorizationProvider);

    private final RestApi api = new RestApi(commandExecutor, authorizationProvider);

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        api.start();
    }

}
