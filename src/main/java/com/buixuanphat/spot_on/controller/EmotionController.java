package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.service.EmotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmotionController {

    EmotionService emotionService;

    @PostMapping("/emotions")
    ApiResponse<Boolean> create (@RequestBody Map<String, String> request)
    {
        int postId = Integer.parseInt(request.get("postId"));
        int userId = Integer.parseInt(request.get("userId"));
        return ApiResponse.<Boolean>builder().data(emotionService.create(postId, userId)).build();
    }


}
