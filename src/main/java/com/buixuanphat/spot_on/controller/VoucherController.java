package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.voucher.CreateVoucherDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.Voucher;
import com.buixuanphat.spot_on.service.VoucherService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherController {
    VoucherService voucherService;

    @PostMapping("/vouchers")
    ApiResponse<VoucherResponseDTO> create (@Valid @RequestBody CreateVoucherDTO request) {
        return ApiResponse.<VoucherResponseDTO>builder().data(voucherService.create(request)).build();
    }

    @GetMapping("/vouchers")
    ApiResponse<List<VoucherResponseDTO>> getVouchersByOrganizer(@RequestParam int organizerId, @Nullable @RequestParam String code){
        return ApiResponse.<List<VoucherResponseDTO>>builder().data(voucherService.getVouchersByOrganizer(organizerId, code)).build();
    }


    @DeleteMapping("/vouchers/{id}")
    ApiResponse<String> delete(@PathVariable int id){
        return ApiResponse.<String>builder().data(voucherService.delete(id)).build();
    }


    @GetMapping("/vouchers/{id}")
    ApiResponse<VoucherResponseDTO> get(@PathVariable int id){
        return ApiResponse.<VoucherResponseDTO>builder().data(voucherService.getVoucher(id)).build();
    }


    @GetMapping("/vouchers/code/{code}")
    ApiResponse<VoucherResponseDTO> getByCode(@PathVariable String code, @RequestParam int userId, @RequestParam int eventId){
        return ApiResponse.<VoucherResponseDTO>builder().data(voucherService.getVoucherByCode(userId, code, eventId)).build();
    }


    @PatchMapping("/vouchers/{id}")
    ApiResponse<VoucherResponseDTO> update(@PathVariable int id, @Valid @RequestBody CreateVoucherDTO request){
        return ApiResponse.<VoucherResponseDTO>builder().data(voucherService.update(id, request)).build();
    }
}
