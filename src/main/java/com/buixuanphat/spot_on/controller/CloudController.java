package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.service.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CloudController {

    CloudinaryService cloudinaryService;

    @PostMapping("/cloud/image")
    ApiResponse<String> uploadImage(@RequestBody MultipartFile image)
    {
        Map<String,String> map = cloudinaryService.uploadImage(image);
        return ApiResponse.<String>builder().data(map.get("url")).build();
    }

}
