package org.lab.data.repository;


import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.Ticket;
import org.lab.data.entity.TicketDeveloper;

import java.util.List;

@RegisterBeanMapper(Ticket.class)
public interface TicketRepository {

    @SqlQuery("""
            SELECT * FROM tickets WHERE ticket_id = :ticketId
            """)
    Ticket findById(long ticketId);

    @SqlQuery("""
            INSERT INTO tickets (milestone_id, name, description)
            VALUES (:ticket.milestoneId, :ticket.name, :ticket.description) RETURNING ticket_id
            """)
    Long createTicket(Ticket ticket);

    @SqlUpdate("""
            UPDATE tickets SET milestone_id = :ticket.milestoneId, name = :ticket.name,
            description = :ticket.description WHERE ticket_id = :ticket.ticketId
            """)
    void updateTicket(Ticket ticket);

    @SqlQuery("""
            SELECT * FROM tickets WHERE milestone_id = :milestoneId
            """)
    List<Ticket> findTicketsByMilestone(long milestoneId);

    @SqlQuery("""
            SELECT t.* FROM tickets t
            JOIN ticket_developers td ON t.ticket_id = td.ticket_id
            WHERE td.user_id = :userId
            """)
    List<Ticket> findTicketsByDeveloper(long userId);

    @SqlUpdate("""
            INSERT INTO ticket_developers (ticket_id, user_id)
            VALUES (#{ticketId}, #{userId}) ON CONFLICT (ticket_id, user_id) DO NOTHING
            """)
    void addDeveloperToTicket(TicketDeveloper ticketDeveloper);

    @SqlUpdate("""
            DELETE FROM ticket_developers WHERE ticket_id = :ticketId AND user_id = :userId
            """)
    void removeDeveloperFromTicket(long ticketId, long userId);

    @SqlQuery("""
            SELECT user_id FROM ticket_developers WHERE ticket_id = :ticketId
            """)
    List<Long> getTicketDevelopers(long ticketId);
}
