package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.section.CreateSectionDTO;
import com.buixuanphat.spot_on.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EventController {

    EventService eventService;

    @Value("${pagination.page-size}")
    @NonFinal
    int pageSize;

    @PostMapping(value = "/events/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<EventResponseDTO> register (@Valid @ModelAttribute CreateEventDTO request){
        return ApiResponse.<EventResponseDTO>builder()
                .data(eventService.register(request))
                .build();
    }

    @PatchMapping("/events/verify/{eventId}")
    ApiResponse<EventResponseDTO> verify(@PathVariable Integer eventId, @RequestParam boolean accept)
    {
        return ApiResponse.<EventResponseDTO>builder()
                .data(eventService.verify(eventId, accept))
                .build();
    }


    @GetMapping("/events")
    ApiResponse<Page<EventResponseDTO>> getEvents(@RequestParam @Nullable Integer id, @RequestParam @Nullable String name, @RequestParam String status ,@RequestParam @Nullable Boolean active, @RequestParam(defaultValue = "0") Integer page)
    {
        return ApiResponse.<Page<EventResponseDTO>>builder()
                .data(eventService.getEvents(id, name,status ,active, page ,pageSize))
                .build();
    }



}
