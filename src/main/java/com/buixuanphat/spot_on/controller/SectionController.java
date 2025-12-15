package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.section.CreateSectionDTO;
import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.service.SectionService;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SectionController {

    SectionService sectionService;

    @GetMapping("/sections")
    ApiResponse<List<SectionResponseDTO>> getSections(@RequestParam("eventId") @Nullable Integer eventId) {
        return ApiResponse.<List<SectionResponseDTO>>builder()
                .data(sectionService.getSections(eventId))
                .build();
    }



    @PostMapping("/sections")
    ApiResponse<SectionResponseDTO> create(@RequestBody CreateSectionDTO request) {
        return ApiResponse.<SectionResponseDTO>builder()
                .data(sectionService.createSection(request))
                .build();
    }


    @DeleteMapping("/sections/{id}")
    ApiResponse<String> create(@PathVariable int id) {
        return ApiResponse.<String>builder()
                .data(sectionService.delete(id))
                .build();
    }
}
