package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer>, JpaSpecificationExecutor<Organizer> {
    boolean existsByEmail(String email);
    boolean existsByTaxCode(String taxCode);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByBankNumberAndBank(String bankNumber, String bank);
    boolean existsByName(String name);

}
