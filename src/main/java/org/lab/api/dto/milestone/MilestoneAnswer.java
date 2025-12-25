package org.lab.api.dto.milestone;

import org.lab.data.entity.MilestoneStatus;

import java.time.Instant;

public record MilestoneAnswer(
        long id,
        String name,
        String description,
        Instant startTime,
        Instant endTime,
        MilestoneStatus status
) {
}
