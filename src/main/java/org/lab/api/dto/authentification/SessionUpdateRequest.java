package org.lab.api.dto.authentification;

public record SessionUpdateRequest(
        String refreshToken
) {
}
