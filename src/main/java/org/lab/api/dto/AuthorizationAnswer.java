package org.lab.api.dto;

public record AuthorizationAnswer(
        UserAnswer user,
        String accessToken,
        String refreshToken
) {
}
