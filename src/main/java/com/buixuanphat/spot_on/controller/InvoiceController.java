package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.invoice.CreateInvoiceRequestDTO;
import com.buixuanphat.spot_on.dto.invoice.InvoiceResponseDTO;
import com.buixuanphat.spot_on.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceController {

    InvoiceService invoiceService;

    @PostMapping("/invoices")
    ApiResponse<InvoiceResponseDTO> create (@RequestBody CreateInvoiceRequestDTO request)
    {
        return ApiResponse.<InvoiceResponseDTO>builder().data(invoiceService.create(request)).build();
    }


}
