package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.invoice.CreateInvoiceRequestDTO;
import com.buixuanphat.spot_on.dto.invoice.InvoiceResponseDTO;
import com.buixuanphat.spot_on.dto.invoice.TicketInfoByEventDTO;
import com.buixuanphat.spot_on.dto.stats.PaymentDateStat;
import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.service.InvoiceService;
import com.buixuanphat.spot_on.service.TicketService;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceController {

    InvoiceService invoiceService;

    TicketService ticketService;

    @PostMapping("/invoices")
    ApiResponse<InvoiceResponseDTO> create (@RequestBody CreateInvoiceRequestDTO request)
    {
        return ApiResponse.<InvoiceResponseDTO>builder().data(invoiceService.create(request)).build();
    }


    @GetMapping("/invoices/user/{userId}")
    ApiResponse<List<InvoiceResponseDTO>> getByUser (@PathVariable int userId, @RequestParam @Nullable String status)
    {
        return ApiResponse.<List<InvoiceResponseDTO>>builder().data(invoiceService.getInvoiceByUser(userId, status)).build();
    }


    @GetMapping("/invoices/{id}")
    ApiResponse<InvoiceResponseDTO> getById (@PathVariable int id)
    {
        return ApiResponse.<InvoiceResponseDTO>builder().data(invoiceService.getById(id)).build();
    }


    @GetMapping("/invoices/{id}/tickets")
    ApiResponse<List<TicketResponseDTO>> getTickets (@PathVariable int id)
    {
        return ApiResponse.<List<TicketResponseDTO>>builder().data(ticketService.getTicketOfInvoice(id)).build();
    }


    @GetMapping("/invoices/stats/user/month/{userId}")
    ApiResponse<List<PaymentDateStat>> getUserStatsForMonth (@PathVariable int userId, @RequestParam int year)
    {
        return ApiResponse.<List<PaymentDateStat>>builder().data(invoiceService.getUserStatsForMonth(userId, year)).build();
    }


    @GetMapping("/invoices/stats/user/year/{userId}")
    ApiResponse<List<PaymentDateStat>> getUserStatsForYear (@PathVariable int userId)
    {
        return ApiResponse.<List<PaymentDateStat>>builder().data(invoiceService.getUserStatsForYear(userId)).build();
    }


    @GetMapping("/invoices/ticket-info/{eventId}")
    ApiResponse<List<TicketInfoByEventDTO>> getTicketInfoByEvent (@PathVariable int eventId)
    {
        return ApiResponse.<List<TicketInfoByEventDTO>>builder().data(invoiceService.getTicketInfoByEvent(eventId)).build();
    }

}
