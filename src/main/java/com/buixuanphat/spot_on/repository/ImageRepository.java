package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {
    List<Image> findAllByPost_Id(Integer id);
}
