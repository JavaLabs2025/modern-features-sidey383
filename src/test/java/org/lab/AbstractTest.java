package org.lab;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
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

    protected static JwtService jwtService;

    protected static AuthorizationProvider authorizationProvider;

    protected static DatabaseProvider dataProvider;

    private static CommandExecutor commandExecutor;

    private static RestApi api;

    @Order(0)
    @BeforeAll
    public static void setup() {
        jwtService = new JwtService(
                new JwtProperty("cn5lLwe7rXKsI4CNQ9WAUTuWR6Tf6W/h/4tSj1d6qCQ="),
                new TokenProperty(Duration.ofMinutes(15)),
                clock
        );
        authorizationProvider = new AuthorizationProvider(jwtService);
        dataProvider = new DatabaseProvider(database.getJdbcUrl(), database.getUsername(), database.getPassword());
        commandExecutor = new CommandExecutor(jwtService, dataProvider, authorizationProvider);
        api = new RestApi(commandExecutor, authorizationProvider);
        RestAssured.baseURI = "http://localhost:8080";
        api.start();
    }

}
