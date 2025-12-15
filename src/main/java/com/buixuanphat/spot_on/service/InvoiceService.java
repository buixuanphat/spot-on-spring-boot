package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.invoice.CreateInvoiceRequestDTO;
import com.buixuanphat.spot_on.dto.invoice.InvoiceResponseDTO;
import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.*;
import com.buixuanphat.spot_on.enums.InvoiceStatus;
import com.buixuanphat.spot_on.enums.TicketStatus;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.SectionMapper;
import com.buixuanphat.spot_on.repository.*;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {

    TicketRepository ticketRepository;

    SectionRepository sectionRepository;

    InvoiceRepository  invoiceRepository;

    VoucherRepository voucherRepository;

    UserRepository userRepository;

    SectionMapper sectionMapper;

    @Transactional
    public InvoiceResponseDTO create(CreateInvoiceRequestDTO request)
    {
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        Invoice invoice = new  Invoice();
        invoice.setUser(user);
        invoice.setTotalPayment(request.getTotalPayment());
        invoice.setStatus(InvoiceStatus.pending.name());
        invoice.setCreatedDate(Instant.now());
        Voucher voucher = new   Voucher() ;
        if(request.getVoucherId()!=null)
        {
            voucher = voucherRepository.findById(request.getVoucherId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));
            invoice.setVoucher(voucher);
        }
        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<Ticket> tickets = new ArrayList<>();
        request.getTickets().forEach(t->
        {
            Section section = sectionRepository.findById(t.getSectionId())
                    .orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy loại vé"));

            Ticket ticket = Ticket.builder()
                    .section(section)
                    .status(TicketStatus.booked.name())
                    .createdDate(Instant.now())
                    .invoice(savedInvoice)
                    .build();

            tickets.add(ticketRepository.save(ticket));
        });


        InvoiceResponseDTO response = InvoiceResponseDTO.builder()
                .id(savedInvoice.getId())
                .status(savedInvoice.getStatus())
                .totalPayment(savedInvoice.getTotalPayment())
                .build();

        UserPublicInfoResponseDTO userResponse = UserPublicInfoResponseDTO
                .builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .createdDate(DateUtils.instantToString(user.getCreatedDate()))
                .active(user.getActive())
                .build();
        response.setUser(userResponse);

        if(voucher!=null)
        {
            VoucherResponseDTO voucherResponse = VoucherResponseDTO.builder()
                    .id(voucher.getId())
                    .code(voucher.getCode())
                    .description(voucher.getDescription())
                    .effectiveDate(DateUtils.instantToString(voucher.getEffectiveDate()))
                    .expirationDate(DateUtils.instantToString(voucher.getExpirationDate()))
                    .limitUsed(voucher.getLimitUsed())
                    .type(voucher.getType())
                    .value(voucher.getValue())
                    .build();
            response.setVoucher(voucherResponse);
        }

        List<TicketResponseDTO> ticketsResponse = new ArrayList<>();
        tickets.forEach(t -> {
            TicketResponseDTO ticketResponse = new TicketResponseDTO();
            ticketResponse.setId(t.getId());
            ticketResponse.setStatus(t.getStatus());
            ticketResponse.setCreatedDate(DateUtils.instantToString(t.getCreatedDate()));
            ticketResponse.setInvoiceId(savedInvoice.getId());


            SectionResponseDTO sectionResponse = sectionMapper.toSectionResponseDTO(sectionRepository.findById(t.getSection().getId())
                    .orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy loại vé")));
            ticketResponse.setSection(sectionResponse);

            ticketsResponse.add(ticketResponse);
        });
        response.setTickets(ticketsResponse);
        return response;
    }





}
