package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    boolean existsByCode(String code);

    Optional<List<Voucher>> findAllByOrganizer_Id(int organizerId);

    Optional<List<Voucher>> findAllByCodeContainingIgnoreCaseAndOrganizer_Id(String code, int organizerId);
}
