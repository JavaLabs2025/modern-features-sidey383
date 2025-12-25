package org.lab.data.entity;

public record BugReport(
        long id,
        long projectId,
        String name,
        String description,
        BugReportStatus status
) {
}
