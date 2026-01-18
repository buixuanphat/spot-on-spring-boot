package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.dto.stats.AdminPaymentStat;
import com.buixuanphat.spot_on.dto.stats.AdminTicketStat;
import com.buixuanphat.spot_on.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
    List<Event> findALlByStatus(String name);

    @Query(
            value = """
                    SELECT
                        event.id,
                        event.name,
                        SUM(invoice.total_payment) AS total
                    FROM event
                    JOIN invoice ON event.id = invoice.event_id
                    WHERE MONTH(invoice.created_date) = :month
                      AND YEAR(invoice.created_date) = :year
                      AND (invoice.status = 'paid' OR invoice.status = 'expired')
                    GROUP BY event.id, event.name;
                    """,
            nativeQuery = true
    )
    List<AdminPaymentStat> getEventPaymentStat(@Param("month") int month, @Param("year") int year);

    @Query(
            value = """
                    SELECT
                        event.id,
                        event.name,
                        COUNT(ticket.id) AS total
                    FROM event
                    JOIN invoice ON event.id = invoice.event_id
                    JOIN ticket ON invoice.id = ticket.invoice_id
                    WHERE MONTH(invoice.created_date) = :month
                      AND YEAR(invoice.created_date) = :year
                      AND (invoice.status = 'paid' OR invoice.status = 'expired')
                    GROUP BY event.id, event.name;
                    """,
            nativeQuery = true
    )
    List<AdminTicketStat> getEventTicketStat(@Param("month") int month, @Param("year") int year);

    List<Event> findTop10ByOrderByIdDesc();


    @Query(
            value = """
                    SELECT e.*
                    FROM event e
                    JOIN invoice i ON e.id = i.event_id
                    JOIN ticket t ON i.id = t.invoice_id
                    WHERE MONTH(i.created_date) = :month
                      AND YEAR(i.created_date) = :year
                      AND i.status = 'paid'
                    GROUP BY e.id
                    ORDER BY COUNT(t.id) DESC
                    LIMIT 10
                    """,
            nativeQuery = true
    )
    List<Event> getTopSale(@Param("month") int month, @Param("year") int year);

}
