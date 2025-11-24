package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.user.CreateCustomerRequestDTO;
import com.buixuanphat.spot_on.dto.user.CreateUserRequestDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @Value("${pagination.page-size}")
    @NonFinal
    int pageSize;
    @PostMapping("/users/register")
    ApiResponse<UserResponseDTO> register(@RequestBody @Valid CreateCustomerRequestDTO request)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .data(userService.createCustomer(request))
                .build();
    }

    @GetMapping("/users")
    ApiResponse<Page<UserResponseDTO>>  getUsers(@RequestParam @Nullable String email, @RequestParam(defaultValue = "0") int page)
    {

        return ApiResponse.<Page<UserResponseDTO>>builder()
                .success(true)
                .data(userService.getUsers(email, page, pageSize))
                .build();

    }


    @PatchMapping(value = "/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponseDTO> updateAvatar(@PathVariable int userId, @Nullable @RequestParam("avatar") MultipartFile avatar)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .data(userService.updateAvatar(userId, avatar))
                .build();
    }


    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponseDTO> createUser(@Valid @ModelAttribute CreateUserRequestDTO request,
                                            @RequestParam MultipartFile file)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .data(userService.createUser(request,file))
                .build();
    }


}
