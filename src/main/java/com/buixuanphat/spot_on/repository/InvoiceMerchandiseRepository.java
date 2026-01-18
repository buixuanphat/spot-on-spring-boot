package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.InvoiceMerchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceMerchandiseRepository extends JpaRepository<InvoiceMerchandise, Integer> {
    List<InvoiceMerchandise> findAllByInvoice_Id(int invoiceId);
}
