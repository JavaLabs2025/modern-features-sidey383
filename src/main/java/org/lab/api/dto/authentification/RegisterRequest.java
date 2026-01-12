package org.lab.api.dto.authentification;

public record RegisterRequest(
        String username,
        String password
) {
}
