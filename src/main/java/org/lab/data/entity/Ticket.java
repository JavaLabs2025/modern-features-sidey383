package org.lab.data.entity;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder(toBuilder = true)
public record Ticket(
        long ticketId,
        long milestoneId,
        @NotNull
        String name,
        @NotNull
        String description,
        TicketStatus status
) {
}
