package org.lab.api.dto.ticket;

public record TicketAssignRequest(
        long ticketId,
        long userId
) {
}
