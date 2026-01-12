package org.lab.data.repository;


import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.Ticket;
import org.lab.data.entity.TicketDeveloper;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(Ticket.class)
@RegisterConstructorMapper(TicketDeveloper.class)
public interface TicketRepository {

    @SqlQuery("""
            SELECT * FROM tickets WHERE ticket_id = :ticketId
            """)
    Optional<Ticket> findById(@Bind("ticketId") long ticketId);

    @SqlQuery("""
            INSERT INTO tickets (milestone_id, name, description, status)
            VALUES (:ticket.milestoneId, :ticket.name, :ticket.description, :ticket.status) RETURNING ticket_id
            """)
    Long createTicket(@BindMethods("ticket") Ticket ticket);

    @SqlUpdate("""
            UPDATE tickets SET
            name = :ticket.name,
            description = :ticket.description,
            status = :ticket.status
            WHERE ticket_id = :ticket.ticketId
            """)
    void updateTicket(@BindMethods("ticket") Ticket ticket);

    @SqlQuery("""
            SELECT * FROM tickets WHERE milestone_id = :milestoneId
            """)
    List<Ticket> findTicketsByMilestone(@Bind("milestoneId") long milestoneId);

    @SqlQuery("""
            SELECT t.* FROM tickets t
            JOIN ticket_developers td ON t.ticket_id = td.ticket_id
            WHERE td.user_id = :userId
            """)
    List<Ticket> findTicketsByDeveloper(@Bind("userId") long userId);

    @SqlUpdate("""
            INSERT INTO ticket_developers (ticket_id, user_id)
            VALUES (:ticketDeveloper.ticketId, :ticketDeveloper.userId) ON CONFLICT (ticket_id, user_id) DO NOTHING
            """)
    void addDeveloperToTicket(@BindMethods("ticketDeveloper") TicketDeveloper ticketDeveloper);

    @SqlUpdate("""
            DELETE FROM ticket_developers WHERE ticket_id = :ticketId AND user_id = :userId
            """)
    void removeDeveloperFromTicket(@Bind("ticketId") long ticketId, @Bind("userId") long userId);

    @SqlQuery("""
            SELECT * FROM ticket_developers WHERE ticket_id = :ticketId
            """)
    List<TicketDeveloper> getTicketDevelopers(@Bind("ticketId") long ticketId);
}
