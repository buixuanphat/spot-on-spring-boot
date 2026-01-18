package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.service.UserGenreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserGenreController {

    UserGenreService userGenreService;

    @PostMapping("/user-genre/{eventId}/{userId}/{action}")
    ApiResponse<Void> interactive (@PathVariable Integer eventId, @PathVariable Integer userId, @PathVariable String action) {
        return ApiResponse.<Void>builder().data(userGenreService.interactive(eventId, userId, action)).build();
    }


    @GetMapping("/user-genre/recomment/{userId}")
    ApiResponse<List<EventResponseDTO>> recomment (@PathVariable Integer userId) {
        return ApiResponse.<List<EventResponseDTO>>builder().data(userGenreService.getRecomment(userId)).build();
    }

}
