package org.lab.api.authorization;

import org.lab.data.entity.User;
import org.lab.data.entity.UserType;

public record Authorization(
        long userId,
        String username,
        UserType userType
) {

    public Authorization(User user) {
        this(user.userId(), user.username(), user.userType());
    }

}
