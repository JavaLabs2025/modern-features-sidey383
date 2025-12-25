package org.lab.data.entity;

import java.time.Instant;

public record Session(
        String sessionId,
        long userId,
        Instant createdAt,
        Instant expiresAt,
        String ipAddress,
        String userAgent,
        boolean active
) {

    public boolean isValid() {
        return active && Instant.now().isBefore(expiresAt);
    }

}
