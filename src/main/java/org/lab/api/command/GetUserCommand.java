package org.lab.api.command;

import lombok.RequiredArgsConstructor;
import org.lab.data.DatabaseProvider;
import org.lab.data.entity.User;

@RequiredArgsConstructor
public final class GetUserCommand implements GetCommand<User> {

    private final long userId;

    @Override
    public User execute(DatabaseProvider databaseProvider) {
        return databaseProvider.getUserRepository().findById(userId).orElseThrow(() ->
                new IllegalStateException("User not found by id " + userId)
        );
    }

}
