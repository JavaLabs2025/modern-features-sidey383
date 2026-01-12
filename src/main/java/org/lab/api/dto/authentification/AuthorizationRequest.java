package org.lab.api.dto.authentification;

public record AuthorizationRequest(
        String username,
        String password
) {
}
