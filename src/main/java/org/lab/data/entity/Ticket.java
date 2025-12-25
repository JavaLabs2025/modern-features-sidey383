package org.lab.data.entity;

import org.jetbrains.annotations.NotNull;

public record Ticket(
        long ticketId,
        long milestoneId,
        @NotNull
        String name,
        @NotNull
        String description
) {
}
