package org.lab.data.entity;

import org.jetbrains.annotations.NotNull;

public record ProjectUser(
        long projectId,
        long userId,
        @NotNull
        ProjectRole projectRole,
        boolean active
) {
}
