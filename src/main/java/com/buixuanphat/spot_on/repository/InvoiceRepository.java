package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.dto.invoice.TicketInfoByEventDTO;
import com.buixuanphat.spot_on.dto.stats.PaymentDateStat;
import com.buixuanphat.spot_on.entity.Invoice;
import com.buixuanphat.spot_on.enums.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    List<Invoice> findALlByStatusAndCreatedDateBefore(String pending, Instant expiredTime);

    List<Invoice> findAllByUser_Id(int userId);

    Invoice findAllByUser_IdAndVoucher_Id(int userId, Integer id);

    List<Invoice> findAllByVoucher_Id(Integer id);

    List<Invoice> findAllByUser_IdAndStatus(int userId, String status);


    List<Invoice> findALlByStatusAndEvent_Status(String name, String name1);


    @Query(
            value = """
                        WITH RECURSIVE months AS (
                            SELECT DATE(CONCAT(:year, '-01-01')) AS month_date
                            UNION ALL
                            SELECT DATE_ADD(month_date, INTERVAL 1 MONTH)
                            FROM months
                            WHERE month_date < DATE(CONCAT(:year, '-12-01'))
                        )
                        SELECT
                            DATE_FORMAT(m.month_date, '%Y-%m') AS label,
                            COALESCE(SUM(i.total_payment), 0) AS total
                        FROM months m
                        LEFT JOIN invoice i
                            ON DATE_FORMAT(i.created_date, '%Y-%m') = DATE_FORMAT(m.month_date, '%Y-%m')
                            AND i.user_id = :userId
                        GROUP BY label
                        ORDER BY label ASC
                    """,
            nativeQuery = true
    )
    List<PaymentDateStat> getUserStatsForMonth(
            @Param("userId") int userId,
            @Param("year") int year
    );


    @Query(
            value = """
                        SELECT DATE_FORMAT(created_date, '%Y') AS label,
                               SUM(total_payment) AS total
                        FROM invoice
                        WHERE user_id = :userId
                        GROUP BY label
                        ORDER BY label ASC
                    """,
            nativeQuery = true
    )
    List<PaymentDateStat> getUserStatsForYear(@Param("userId") int userId);

    List<Invoice> findAllByStatus(String name);


    @Query(
            value = """
            SELECT
                t.id,
                u.firstname,
                u.lastname,
                u.email,
                s.name AS section_name,
                t.status
            FROM invoice i
            JOIN user u ON i.user_id = u.id
            JOIN ticket t ON i.id = t.invoice_id
            JOIN section s ON t.section_id = s.id
            WHERE i.event_id = :eventId and i.status!='pending';
               \s""",
            nativeQuery = true
    )
    List<TicketInfoByEventDTO> getTicketInfoByEvent(@Param("eventId") int eventId);


}

