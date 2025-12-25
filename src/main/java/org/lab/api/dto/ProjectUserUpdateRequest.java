package org.lab.api.dto;

public record ProjectUserUpdateRequest(
        long projectId,
        Long userId,
        String username
) {
}
