package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.section.CreateSectionDTO;
import com.buixuanphat.spot_on.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    @PostMapping(value = "/events", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<EventResponseDTO> createEvent(@Valid @RequestParam String request,
                                              @RequestParam MultipartFile image,
                                              @RequestParam MultipartFile license,
                                              @Valid @RequestParam String sections){
        return ApiResponse.<EventResponseDTO>builder()
                .success(true)
                .data(eventService.createEvent(request, image, license, sections))
                .build();
    }

    @PatchMapping("/events/verify/{eventId}")
    ApiResponse<EventResponseDTO> verify(@PathVariable Integer eventId, @RequestParam boolean accept)
    {
        return ApiResponse.<EventResponseDTO>builder()
                .success(true)
                .data(eventService.verify(eventId, accept))
                .build();
    }



}
