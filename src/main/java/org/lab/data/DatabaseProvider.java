package org.lab.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.lab.data.repository.*;

import java.io.Closeable;

@Slf4j
public class DatabaseProvider implements Closeable {

    private final HikariDataSource dataSource;
    @Getter
    private final Jdbi jdbi;
    @Getter
    private final UserRepository userRepository;
    @Getter
    private final ProjectRepository projectRepository;
    @Getter
    private final ProjectUserRepository projectUserRepository;
    @Getter
    private final MilestoneRepository milestoneRepository;
    @Getter
    private final TicketRepository ticketRepository;
    @Getter
    private final BugReportRepository bugReportRepository;
    @Getter
    private final SessionRepository sessionRepository;

    public DatabaseProvider() {
        this("jdbc:postgresql://localhost:5432/features", "postgres", "12345");
    }

    public DatabaseProvider(String url, String username, String password) {
        runMigration(url, username, password);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);

        this.jdbi = Jdbi.create(dataSource)
                .installPlugin(new Jackson2Plugin())
                .installPlugin(new SqlObjectPlugin());
        userRepository = jdbi.onDemand(UserRepository.class);
        projectRepository = jdbi.onDemand(ProjectRepository.class);
        projectUserRepository = jdbi.onDemand(ProjectUserRepository.class);
        milestoneRepository = jdbi.onDemand(MilestoneRepository.class);
        ticketRepository = jdbi.onDemand(TicketRepository.class);
        bugReportRepository = jdbi.onDemand(BugReportRepository.class);
        sessionRepository = jdbi.onDemand(SessionRepository.class);

        log.info("Database initialized");
    }

    private void runMigration(String url, String username, String password) {
        final FluentConfiguration config = new FluentConfiguration()
                .dataSource(url, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .table("flyway_schema_history");

        final Flyway flyway = new Flyway(config);
        try {
            log.info("Starting database migration...");
            flyway.migrate();
            log.info("Database migration completed successfully.");
        } catch (Exception e) {
            log.error("Database migration failed!", e);
        }
    }

    @Override
    public void close() {
        dataSource.close();
    }

}
