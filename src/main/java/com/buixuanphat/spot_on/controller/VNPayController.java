package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.vnpay.VNPayRequest;
import com.buixuanphat.spot_on.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {

    private final VNPayService vnPayService;

    @PostMapping("/vnpay/create-payment")
    public ApiResponse<String> createPayment(@RequestBody VNPayRequest req, HttpServletRequest servletReq) throws UnsupportedEncodingException {
        String paymentUrl = vnPayService.createPaymentUrl(req, servletReq);
        return ApiResponse.<String>builder().data(paymentUrl).build();
    }


    @GetMapping("/vnpay/ipn")
    public Map<String, String> vnpayIPN(@RequestParam Map<String, String> allParams) {
        return vnPayService.processIPN(allParams);
    }


    @GetMapping("/vnpay/return")
    public String returnUrl() {
        return "Thanh toán thành công";
    }



}