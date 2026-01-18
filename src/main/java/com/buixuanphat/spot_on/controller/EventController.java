package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.stats.AdminPaymentStat;
import com.buixuanphat.spot_on.dto.stats.AdminTicketStat;
import com.buixuanphat.spot_on.service.EventService;
import com.buixuanphat.spot_on.service.TicketService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EventController {

    EventService eventService;

    TicketService ticketService;

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

    @GetMapping("/events/{id}")
    ApiResponse<EventResponseDTO> getEvent(@PathVariable Integer id){
        return ApiResponse.<EventResponseDTO>builder()
                .data(eventService.getEvent(id))
                .build();
    }

    @GetMapping("/events/new")
    ApiResponse<List<EventResponseDTO>> getNewEvent(){
        return ApiResponse.<List<EventResponseDTO>>builder()
                .data(eventService.getNew())
                .build();
    }

    @GetMapping("/events/top")
    ApiResponse<List<EventResponseDTO>> getTopSale(){
        return ApiResponse.<List<EventResponseDTO>>builder()
                .data(eventService.getTopSale())
                .build();
    }

    @GetMapping("/events")
    ApiResponse<Page<EventResponseDTO>> getEvents(@RequestParam @Nullable Integer organizerId ,
                                                  @RequestParam @Nullable Integer id,
                                                  @RequestParam @Nullable String name,
                                                  @RequestParam @Nullable String status,
                                                  @RequestParam @Nullable String genre,
                                                  @RequestParam @Nullable String province,
                                                  @RequestParam(defaultValue = "0") Integer page)
    {
        return ApiResponse.<Page<EventResponseDTO>>builder()
                .data(eventService.getEvents(organizerId , id, name,status, genre, province , page ,pageSize))
                .build();
    }



    @PatchMapping("/events/run/{id}")
    ApiResponse<EventResponseDTO> run(@PathVariable int id)
    {
        return ApiResponse.<EventResponseDTO>builder()
                .data(eventService.run(id))
                .build();
    }



    @GetMapping("/events/stats/payment")
    ApiResponse<List<AdminPaymentStat>> getEventPaymentStat(@RequestParam int month, @RequestParam int year)
    {
        return ApiResponse.<List<AdminPaymentStat>>builder()
                .data(eventService.getEventPaymentStat(month, year))
                .build();
    }



    @GetMapping("/events/stats/ticket")
    ApiResponse<List<AdminTicketStat>> getEventTicketStat(@RequestParam int month, @RequestParam int year)
    {
        return ApiResponse.<List<AdminTicketStat>>builder()
                .data(eventService.getEventTicketStat(month, year))
                .build();
    }


    @PatchMapping("/check-in")
    ApiResponse<Boolean> checkIn(@RequestParam int id){
        return ApiResponse.<Boolean>builder().data(ticketService.check(id)).build();
    }


}
