package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.dto.stats.AdminPaymentStat;
import com.buixuanphat.spot_on.dto.stats.AdminTicketStat;
import com.buixuanphat.spot_on.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer>, JpaSpecificationExecutor<Organizer> {
    boolean existsByEmail(String email);

    boolean existsByBankNumberAndBank(String bankNumber, String bank);

    boolean existsByName(String name);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByTaxCode(String taxCode);

    Optional<Organizer> findById(int id);

    @Query(
            value = """
                                                       SELECT
                                                          organizer.id,
                                                          organizer.name,
                                                          sum(invoice.total_payment) AS total
                                                      FROM organizer
                                                      JOIN event   ON organizer.id = event.organizer_id
                                                      JOIN invoice ON event.id     = invoice.event_id
                                                      WHERE MONTH(invoice.created_date) = :month
                                                        AND YEAR(invoice.created_date) = :year
                                                        AND invoice.status IN ('paid', 'expired')
                                                      GROUP BY organizer.id, organizer.name;
                    """,
            nativeQuery = true
    )
    List<AdminPaymentStat> getOrganizerPaymentStat(@Param("month") int month, @Param("year") int year);

    @Query(
            value = """
                                                SELECT
                                                       organizer.id,
                                                       organizer.name,
                                                       count(ticket.id) AS total
                                                   FROM organizer
                                                   JOIN event   ON organizer.id = event.organizer_id
                                                   JOIN invoice ON event.id     = invoice.event_id
                                                   join ticket on invoice.id    = ticket.invoice_id
                                                   WHERE MONTH(invoice.created_date) = :month
                                                     AND YEAR(invoice.created_date) = :year
                                                     AND invoice.status IN ('paid', 'expired')
                                                   GROUP BY organizer.id, organizer.name;
                    """,
            nativeQuery = true
    )
    List<AdminTicketStat> getOrganizerTicketStat(@Param("month") int month, @Param("year") int year);

}
