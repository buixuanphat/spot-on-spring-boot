package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.service.UserOrganizerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOrganizerController {

    UserOrganizerService userOrganizerService;

    @GetMapping("/users-organizers/{userId}")
    ApiResponse<OrganizerResponseDTO> getOrganizerByUserId (@PathVariable("userId") int userId)
    {
        return ApiResponse.<OrganizerResponseDTO>builder().data(userOrganizerService.getOrganizer(userId)).build();
    }

}
