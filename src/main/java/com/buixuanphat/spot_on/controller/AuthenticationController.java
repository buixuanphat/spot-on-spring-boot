package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.authentication.AuthenticationRequestDTO;
import com.buixuanphat.spot_on.dto.authentication.AuthenticationResponseDTO;
import com.buixuanphat.spot_on.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/auth/log-in")
    ApiResponse<AuthenticationResponseDTO> login (@RequestBody AuthenticationRequestDTO request) {
        return ApiResponse.<AuthenticationResponseDTO>builder()
                .success(true)
                .data(authenticationService.authenticate(request))
                .build();
    }

}
