package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Voucher;
import com.buixuanphat.spot_on.entity.VoucherEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherEventRepository extends JpaRepository<VoucherEvent,Integer> {
    List<VoucherEvent> findAllByEvent_Id(int eventId);
}
