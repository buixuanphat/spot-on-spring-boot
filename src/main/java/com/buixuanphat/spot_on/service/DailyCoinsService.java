package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.entity.DailyCoins;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.repository.DailyCoinsRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@RequiredArgsConstructor
public class DailyCoinsService {

    DailyCoinsRepository dailyCoinsRepository;

    UserRepository userRepository;

    public Boolean login(int userId)
    {
        User user = userRepository.findById(userId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        DailyCoins dailyCoins = dailyCoinsRepository.findByUserAndCreatedDate(user, LocalDate.now());
        if(dailyCoins == null)
        {
            DailyCoins newDailyCoins = DailyCoins.builder().user(user).createdDate(LocalDate.now()).build();
            dailyCoinsRepository.save(newDailyCoins);
            user.setCoins(user.getCoins()+100);
            userRepository.save(user);

            return true;
        }
        else
        {
            return true;
        }
    }

    public Boolean isLogin(int userId)
    {
        DailyCoins dailyCoins = dailyCoinsRepository.findByUserIdAndCreatedDate(userId, LocalDate.now());
        if(dailyCoins == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }



}
