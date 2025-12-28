package org.lab.api.dto.bug;

public record BugReportUpdateRequest(
        long bugReportId,
        String name,
        String description
) {
}
