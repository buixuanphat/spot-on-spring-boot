package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.dto.voucher_event.VoucherEventResponseDTO;
import com.buixuanphat.spot_on.entity.VoucherEvent;
import com.buixuanphat.spot_on.service.VoucherEventService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherEventController {

    VoucherEventService voucherEventService;

    @PostMapping("/vouchers-events")
    ApiResponse<Map<String, Integer>> create (@RequestBody Map<String, Integer> request)
    {
        return ApiResponse.<Map<String, Integer>>builder().data(voucherEventService.createVoucherEvent(request)).build();
    }

    @GetMapping("/vouchers-events/event")
    ApiResponse<List<VoucherEventResponseDTO>> getVoucherByEvent(@RequestParam int eventId)
    {
        return ApiResponse.<List<VoucherEventResponseDTO>>builder().data(voucherEventService.getVouchersByEvent(eventId)).build();
    }

    @DeleteMapping("/vouchers-events/{id}")
    ApiResponse<String> delete (@PathVariable int id)
    {
        return ApiResponse.<String>builder().data(voucherEventService.delete(id)).build();
    }




}
