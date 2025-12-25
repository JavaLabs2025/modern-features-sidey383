package org.lab.api.dto.milestone;

import java.time.Instant;

public record MilestoneCreateRequest(
        long projectId,
        String name,
        String description,
        Instant startTime,
        Instant endTime
) {
}
