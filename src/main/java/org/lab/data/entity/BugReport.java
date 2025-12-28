package org.lab.data.entity;

import lombok.Builder;

@Builder(toBuilder = true)
public record BugReport(
        long bugId,
        long projectId,
        String name,
        String description,
        BugReportStatus status
) {
}
