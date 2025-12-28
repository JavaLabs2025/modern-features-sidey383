package org.lab.api.dto.bug;

import org.lab.data.entity.BugReportStatus;

public record BugReportAnswer(
        long id,
        String name,
        String description,
        BugReportStatus status
) {
}
