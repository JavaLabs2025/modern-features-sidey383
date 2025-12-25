package org.lab.api.dto;

public record RegisterRequest(
        String username,
        String password
) {
}
