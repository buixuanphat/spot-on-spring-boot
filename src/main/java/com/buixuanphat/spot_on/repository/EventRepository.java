package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
}
