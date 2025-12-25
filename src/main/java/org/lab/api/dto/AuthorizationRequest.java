package org.lab.api.dto;

public record AuthorizationRequest(
        String username,
        String password
) {
}
