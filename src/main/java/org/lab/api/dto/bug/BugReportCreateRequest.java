package org.lab.api.dto.bug;

public record BugReportCreateRequest(
        long projectId,
        String name,
        String description
) {
}
