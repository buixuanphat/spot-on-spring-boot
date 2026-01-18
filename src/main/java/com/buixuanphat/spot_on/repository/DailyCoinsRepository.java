package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.DailyCoins;
import com.buixuanphat.spot_on.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;

public interface DailyCoinsRepository extends JpaRepository<DailyCoins, Integer> {
    DailyCoins findByUserAndCreatedDate(User user, LocalDate now);

    DailyCoins findByUserIdAndCreatedDate(int userId, LocalDate now);
}
