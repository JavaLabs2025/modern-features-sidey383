package org.lab.data.entity;

public record User(
        long userId,
        String username,
        String password,
        UserType userType,
        boolean active
) {
}
