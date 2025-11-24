package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(@Email(message = "USER_EXISTED") String email);
    Optional<User> findByEmail(@Email(message = "USER_EXISTED") String email);
    Page<User> findByEmailContainingIgnoreCaseAndActive(String email, Boolean active, Pageable pageable);
    Page<User> findByActive(Boolean active, Pageable pageable);
}
