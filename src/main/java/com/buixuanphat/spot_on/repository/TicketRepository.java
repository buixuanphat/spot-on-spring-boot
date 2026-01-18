package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllBySection_Id(int sectionID);

    List<Ticket> findAllByInvoice_Id(int invoiceID);
}
