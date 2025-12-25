package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.Project;

import java.util.Optional;

@RequiredArgsConstructor
public final class GetProjectCommand implements DataCommand<Optional<Project>> {

    private final long userId;

    @Override
    public Optional<Project> execute(DatabaseProvider databaseProvider) {
        return databaseProvider.getProjectRepository().findById(userId);
    }

    public DataCommand<Project> nullChecked() {
        return new NullChecked();
    }

    protected final class NullChecked implements DataCommand<Project> {

        @Override
        public Project execute(DatabaseProvider databaseProvider) {
            return GetProjectCommand.this.execute(databaseProvider).orElseThrow(() ->
                    new IllegalStateException("Project not found by id " + userId)
            );
        }
    }

}
