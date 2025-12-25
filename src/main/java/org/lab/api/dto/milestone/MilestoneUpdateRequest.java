package org.lab.api.dto.milestone;

import java.time.Instant;

public record MilestoneUpdateRequest(
        long milestoneId,
        String name,
        String description,
        Instant startTime,
        Instant endTime
) {
}
