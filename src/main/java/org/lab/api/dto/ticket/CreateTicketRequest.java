package org.lab.api.dto.ticket;

public record CreateTicketRequest(
        long milestoneId,
        String name,
        String description
) {
}
