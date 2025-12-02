package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.voucher.CreateVoucherDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.Voucher;
import com.buixuanphat.spot_on.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherController {
    VoucherService voucherService;

    @PostMapping("/vouchers")
    ApiResponse<VoucherResponseDTO> create (@Valid @RequestBody CreateVoucherDTO request) {
        return ApiResponse.<VoucherResponseDTO>builder().data(voucherService.create(request)).build();
    }
}
