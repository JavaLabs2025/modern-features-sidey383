package org.lab.data.entity;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Builder(toBuilder = true)
public record Milestone(
        long milestoneId,
        long projectId,
        String name,
        String description,
        @NotNull
        Instant startTime,
        @NotNull
        Instant endTime,
        @NotNull
        MilestoneStatus status,
        boolean active
) {
}
