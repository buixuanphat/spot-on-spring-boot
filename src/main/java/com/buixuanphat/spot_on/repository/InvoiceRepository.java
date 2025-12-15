package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Invoice;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
