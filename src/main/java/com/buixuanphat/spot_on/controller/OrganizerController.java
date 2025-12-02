package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.organizer.OrganizerCreateRequestDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.service.OrganizerService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizerController {

    OrganizerService organizerService;

    @Value("${pagination.page-size}")
    @NonFinal
    int pageSize;




    @PostMapping(value = "/organizers/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<OrganizerResponseDTO> createOrganizer(@Valid @ModelAttribute OrganizerCreateRequestDTO request)
    {
        return ApiResponse.<OrganizerResponseDTO>builder()
                .data(organizerService.register(request))
                .build();
    }




    @GetMapping("/organizers")
    ApiResponse<Page<OrganizerResponseDTO>> getOrganizers (@RequestParam @Nullable Integer id,
                                                           @RequestParam @Nullable String name,
                                                           @RequestParam @Nullable String status,
                                                           @RequestParam(defaultValue = "0") int page
    )
    {
        return ApiResponse.<Page<OrganizerResponseDTO>>builder()
                .data(organizerService.getOrganizers(id, name, status, page, pageSize))
                .build();
    }

    @GetMapping("/organizers/{id}")
    ApiResponse<OrganizerResponseDTO> getOrganizer(@PathVariable Integer id)
    {
        return ApiResponse.<OrganizerResponseDTO>builder().data(organizerService.getOrganizer(id)).build();
    }


    @PatchMapping("/organizers/verify/{organizerId}")
    ApiResponse<UserResponseDTO> verify(@PathVariable("organizerId") int organizerId, @RequestParam boolean accept)
    {
        return ApiResponse.<UserResponseDTO>builder()
                .data(organizerService.verify(organizerId, accept))
                .build();
    }


}
