package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    List<Section> findAllByEvent_Id(Integer eventId);
}
