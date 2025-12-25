package org.lab.data.entity;

import java.time.Instant;

public record User(
        long userId,
        String username,
        String password,
        UserType userType,
        boolean active,
        Instant createdAt
) {
}
