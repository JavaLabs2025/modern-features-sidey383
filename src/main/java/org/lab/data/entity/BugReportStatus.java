package org.lab.data.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BugReportStatus {
    NEW(0),
    FIXED(1),
    TESTED(2),
    CLOSED(3);
    private final int order;

    public boolean isBefore(BugReportStatus status) {
        return this.order < status.order;
    }
}
