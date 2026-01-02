package org.lab.api.dto.ticket;

public record UpdateTicketRequest(
        long ticketId,
        String name,
        String description
) {
}
