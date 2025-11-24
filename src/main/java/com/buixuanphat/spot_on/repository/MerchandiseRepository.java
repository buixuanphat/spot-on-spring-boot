package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Merchandise;
import com.buixuanphat.spot_on.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Integer>, JpaSpecificationExecutor<Merchandise> {
    Page<Merchandise> getMerchandises(Integer organizerId , String name , Pageable pageable);
}
