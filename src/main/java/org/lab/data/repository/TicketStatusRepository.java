package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.lab.data.entity.TicketStatus;

@RegisterConstructorMapper(TicketStatus.class)
public interface TicketStatusRepository {

    @SqlQuery("""
            SELECT status FROM ticket_status WHERE ticket_id = :ticketId
            """)
    TicketStatus getTicketStatus(long ticketId);

    @SqlQuery("""
            INSERT INTO ticket_status (ticket_id, status)
            VALUES (:ticketId, :status)
            ON CONFLICT (ticket_id) DO UPDATE SET status = :status
            """)
    void setTicketStatus(long ticketId, TicketStatus status);
}
