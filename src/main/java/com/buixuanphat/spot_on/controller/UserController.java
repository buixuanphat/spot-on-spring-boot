package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.user.*;
import com.buixuanphat.spot_on.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
                .data(userService.register(request))
                .build();
    }

    @GetMapping("/users")
    ApiResponse<Page<UserResponseDTO>>  getUsers(@RequestParam(required = false) Integer id ,@RequestParam(required = false) String email, @RequestParam(defaultValue = "0") int page)
    {
        return ApiResponse.<Page<UserResponseDTO>>builder()
                .data(userService.getUsers(id, email, page, pageSize))
                .build();

    }

    @GetMapping("/users/{id}")
    ApiResponse<UserResponseDTO> getUser (@PathVariable Integer id)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .data(userService.getUser(id))
                .build();
    }


    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponseDTO> createUser(@Valid @ModelAttribute CreateUserRequestDTO request)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .data(userService.createUser(request))
                .build();
    }

    @PostMapping(value = "/users/user-organizer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponseDTO> createOrganizerUser(@Valid @ModelAttribute CreateOrganizerUserRequestDTO request)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .data(userService.createOrganizerUser(request))
                .build();
    }

    @PatchMapping(value = "/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponseDTO> update( @Valid @PathVariable(value = "id") int id ,@Valid @ModelAttribute UpdateUserRequestDTO request)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .data(userService.update(id , request))
                .build();
    }


    @PatchMapping("/users/disable/{id}")
    ApiResponse<String> disable( @PathVariable(value = "id") int id)
    {
        return ApiResponse.<String>builder()
                .data(userService.disable(id))
                .build();
    }


}
