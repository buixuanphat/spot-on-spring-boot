package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.event_merchandise.EventMerchandiseResponseDTO;
import com.buixuanphat.spot_on.service.EventMerchandiseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventMerchandiseController {

    EventMerchandiseService eventMerchandiseService;

    @GetMapping("/event-merchandise")
    ApiResponse<List<EventMerchandiseResponseDTO>> get(@RequestParam Integer eventId)
    {
        return ApiResponse.<List<EventMerchandiseResponseDTO>>builder()
                .data(eventMerchandiseService.getMerchandiseByEvent(eventId))
                .build();
    }


    @PostMapping("/event-merchandise")
    ApiResponse<Map<String, String>> create(@RequestBody Map<String, String> request)
    {
        return ApiResponse.<Map<String, String>>builder()
                .data(eventMerchandiseService.createEventMerchandise(request))
                .build();
    }

    @DeleteMapping ("/event-merchandise/{id}")
    ApiResponse<String> delete(@PathVariable Integer id)
    {
        return ApiResponse.<String>builder()
                .data(eventMerchandiseService.deleteEventMerchandise(id))
                .build();
    }

}
