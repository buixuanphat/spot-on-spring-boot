package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.entity.DailyCoins;
import com.buixuanphat.spot_on.service.DailyCoinsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DailyCoinsController {

    DailyCoinsService  dailyCoinsService;

    @PostMapping("/daily-coins/{userId}")
    ApiResponse<Boolean> login (@PathVariable int userId)
    {
        return ApiResponse.<Boolean>builder().data(dailyCoinsService.login(userId)).build();
    }

    @GetMapping("/daily-coins/{userId}")
    ApiResponse<Boolean> isLogin (@PathVariable int userId)
    {
        return ApiResponse.<Boolean>builder().data(dailyCoinsService.isLogin(userId)).build();
    }

}
