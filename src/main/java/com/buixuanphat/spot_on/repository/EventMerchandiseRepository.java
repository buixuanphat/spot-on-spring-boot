package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.EventMerchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventMerchandiseRepository extends JpaRepository<EventMerchandise, Integer> {
    Optional<List<EventMerchandise>> findAllByEvent_Id(int eventId);
}
