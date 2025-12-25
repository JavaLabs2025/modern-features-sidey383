package org.lab.api.command;

import org.lab.data.DatabaseProvider;
import org.lab.data.entity.User;

import java.util.Optional;

public final class GetUserCommand implements DataCommand<Optional<User>> {

    private final Long userId;
    private final String username;

    public GetUserCommand(long userId) {
        this.userId = userId;
        this.username = null;
    }

    public GetUserCommand(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public Optional<User> execute(DatabaseProvider databaseProvider) {
        if (userId != null) {
            return databaseProvider.getUserRepository().findById(userId);
        }
        return databaseProvider.getUserRepository().findByUsername(username);
    }

    public DataCommand<User> nullChecked() {
        return new NullChecked();
    }

    protected final class NullChecked implements DataCommand<User> {

        @Override
        public User execute(DatabaseProvider databaseProvider) {
            return GetUserCommand.this.execute(databaseProvider).orElseThrow(() ->
                    new IllegalStateException("User not found by id " + userId)
            );
        }
    }

}
