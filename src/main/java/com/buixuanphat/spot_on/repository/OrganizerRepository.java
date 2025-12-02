package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

}
