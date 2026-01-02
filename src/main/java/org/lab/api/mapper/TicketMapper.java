package org.lab.api.mapper;

import org.lab.api.dto.ticket.TicketAnswer;
import org.lab.data.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TicketMapper {

    @Mapping(target = "id", source = "ticketId")
    TicketAnswer toAnswer(Ticket ticket);

    List<TicketAnswer> toAnswer(Collection<Ticket> tickets);

}
