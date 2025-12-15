package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.UserOrganizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOrganizerRepository extends JpaRepository<UserOrganizer, Integer> {
    Optional<UserOrganizer> findByUser_Id(Integer id);
}
