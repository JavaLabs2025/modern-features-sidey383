package org.lab.api.dto.authentification;

import org.lab.api.dto.UserAnswer;

public record AuthorizationAnswer(
        UserAnswer user,
        String accessToken,
        String refreshToken
) {
}
