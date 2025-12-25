package org.lab.data.entity;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record Milestone(
        long milestoneId,
        @NotNull
        Instant startTime,
        @NotNull
        Instant endTime,
        @NotNull
        MilestoneStatus status,
        boolean active
) {
}
