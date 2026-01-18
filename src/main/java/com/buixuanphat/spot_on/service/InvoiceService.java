package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.invoice.CreateInvoiceRequestDTO;
import com.buixuanphat.spot_on.dto.invoice.InvoiceResponseDTO;
import com.buixuanphat.spot_on.dto.invoice.TicketInfoByEventDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.dto.stats.PaymentDateStat;
import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.*;
import com.buixuanphat.spot_on.enums.InvoiceStatus;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.enums.TicketStatus;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.mapper.MerchandiseMapper;
import com.buixuanphat.spot_on.mapper.SectionMapper;
import com.buixuanphat.spot_on.mapper.VoucherMapper;
import com.buixuanphat.spot_on.repository.*;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableScheduling
@SpringBootApplication
public class InvoiceService {

    TicketRepository ticketRepository;
    TicketService ticketService;

    InvoiceRepository invoiceRepository;

    VoucherRepository voucherRepository;
    VoucherMapper voucherMapper;
    VoucherService voucherService;

    UserRepository userRepository;
    UserService userService;

    SectionRepository sectionRepository;
    SectionMapper sectionMapper;
    SectionService sectionService;

    MerchandiseRepository merchandiseRepository;
    MerchandiseMapper merchandiseMapper;
    MerchandiseService merchandiseService;

    InvoiceMerchandiseRepository invoiceMerchandiseRepository;

    EventRepository eventRepository;
    EventMapper eventMapper;

    EvaluationRepository evaluationRepository;

    EmailService emailService;

    @Transactional
    public InvoiceResponseDTO create(CreateInvoiceRequestDTO request) {
        // Create invoice
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setTotalPayment(request.getTotalPayment());
        invoice.setStatus(InvoiceStatus.pending.name());
        invoice.setCreatedDate(Instant.now());


        Voucher voucher = new Voucher();
        if (request.getVoucherId() != null) {
            voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));
            invoice.setVoucher(voucher);
        }

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));

        invoice.setEvent(event);

        if (request.getCoins() != null) {
            invoice.setCoins(request.getCoins());
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Create ticket
        List<Ticket> tickets = new ArrayList<>();
        request.getTickets().forEach(t ->
        {
            Section section = sectionRepository.findById(t.getId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy loại vé"));

            for (int i = 0; i < t.getAmount(); i++) {
                Ticket ticket = Ticket.builder()
                        .section(section)
                        .status(TicketStatus.available.name())
                        .createdDate(Instant.now())
                        .invoice(savedInvoice)
                        .build();
                tickets.add(ticketRepository.save(ticket));
            }
        });

        List<Section> sections = sectionRepository.findAllByEvent_Id(savedInvoice.getEvent().getId());
        sections.forEach(s ->
        {
            if (sectionService.getRemaining(s.getId(), s.getTotalSeats()) < 0)
                throw new AppException(HttpStatus.CONFLICT.value(), String.format("Vé %s không còn đủ số lượng. Vui lòng thử lại", s.getName()));
        });


        // Create merchandise
        List<InvoiceMerchandise> invoiceMerchandises = new ArrayList<>();
        if (request.getMerchandises() != null) {
            request.getMerchandises().forEach(m ->
            {
                Merchandise merchandise = merchandiseRepository.findById(m.getId())
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đồ lưu niệm"));

                InvoiceMerchandise invoiceMerchandise = new InvoiceMerchandise();
                invoiceMerchandise.setMerchandise(merchandise);
                invoiceMerchandise.setInvoice(savedInvoice);
                invoiceMerchandises.add(invoiceMerchandiseRepository.save(invoiceMerchandise));
            });
        }


        // Response
        InvoiceResponseDTO response = InvoiceResponseDTO.builder()
                .id(savedInvoice.getId())
                .status(savedInvoice.getStatus())
                .totalPayment(savedInvoice.getTotalPayment())
                .build();


        response.setUser(userService.convertToUserDTO(user.getId()));


        response.setEvent(eventMapper.toEventResponseDTO(savedInvoice.getEvent()));


        if (voucher != null) {
            VoucherResponseDTO voucherResponse = voucherMapper.toVoucherDTO(voucher);
            voucherResponse.setEffectiveDate(DateUtils.instantToString(voucher.getEffectiveDate()));
            voucherResponse.setExpirationDate(DateUtils.instantToString(voucher.getExpirationDate()));
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
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy loại vé")));
            ticketResponse.setSection(sectionResponse);

            ticketsResponse.add(ticketResponse);
        });
        response.setTickets(ticketsResponse);


        List<MerchandiseResponseDTO> merchandiseResponses = new ArrayList<>();
        if (!invoiceMerchandises.isEmpty()) {
            invoiceMerchandises.forEach(im ->
            {
                MerchandiseResponseDTO merchandiseResponse = merchandiseMapper.toMerchandiseResponseDTO(im.getMerchandise());
                merchandiseResponses.add(merchandiseResponse);
            });
        }
        response.setMerchandises(merchandiseResponses);

        System.err.println(merchandiseResponses);

        return response;
    }


    @Scheduled(fixedRate = 60 * 1000)
    public void removeExpiredInvoices() {
        Instant expiredTime = Instant.now().minus(15, ChronoUnit.MINUTES);

        List<Invoice> expiredInvoices =
                invoiceRepository.findALlByStatusAndCreatedDateBefore(Status.pending.name(),
                        expiredTime
                );
        invoiceRepository.deleteAll(expiredInvoices);
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void updateExpiredInvoices() {
        List<Invoice> invoices =
                invoiceRepository.findALlByStatusAndEvent_Status(Status.paid.name(), Status.expired.name());
        invoices.forEach(i ->
        {
            i.setStatus(Status.expired.name());
            invoiceRepository.save(i);

            List<Ticket> tickets = ticketRepository.findAllByInvoice_Id(i.getId());
            tickets.forEach(t -> {
                t.setStatus(Status.expired.name());
                ticketRepository.save(t);
            });
        });

    }


    @Transactional
    @Scheduled(fixedRate = 60 * 1000)
    public void notifyUpcoming() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();


        List<Invoice> invoices =
                invoiceRepository.findAllByStatus(Status.paid.name());

        invoices.forEach(i -> {
            Event event = i.getEvent();

            if (!event.getDate().equals(today)) return;

            LocalTime notifyTime = event.getStartTime().minusHours(6);

            if (now.isAfter(notifyTime) && i.getNotification()<3) {

                String to = i.getUser().getEmail();
                String subject = "NHẮC NHỞ SỰ KIỆN SẮP DIỄN RA";
                String text = String.format(
                        "Sự kiện %s sẽ diễn ra sau %d giờ nữa.",
                        event.getName(), 6-2*i.getNotification()
                );

                emailService.sendSimpleMail(to, subject, text);

                i.setNotification(i.getNotification()+1);
                invoiceRepository.save(i);
            }
        });
    }





    public List<InvoiceResponseDTO> getInvoiceByUser(int userId, String status) {
        List<Invoice> invoices = invoiceRepository.findAllByUser_IdAndStatus(userId, status);

        List<InvoiceResponseDTO> invoiceResponses = new ArrayList<>();


        invoices.forEach(i -> {
            InvoiceResponseDTO invoiceResponse = new InvoiceResponseDTO();
            invoiceResponse.setId(i.getId());

            invoiceResponse.setEvent(eventMapper.toEventResponseDTO(i.getEvent()));

            invoiceResponse.setUser(userService.convertToUserDTO(i.getUser().getId()));

            if (i.getVoucher() != null) {
                VoucherResponseDTO voucherResponse = voucherMapper.toVoucherDTO(i.getVoucher());
                voucherResponse.setEffectiveDate(DateUtils.instantToString(i.getVoucher().getEffectiveDate()));
                voucherResponse.setExpirationDate(DateUtils.instantToString(i.getVoucher().getExpirationDate()));
                invoiceResponse.setVoucher(voucherResponse);
            }


            invoiceResponse.setTickets(ticketService.getTicketOfInvoice(i.getId()));

            invoiceResponse.setMerchandises(merchandiseService.getMerchandisesOfInvoice(i.getId()));

            invoiceResponse.setStatus(i.getStatus());
            invoiceResponse.setTotalPayment(i.getTotalPayment());
            if (i.getPurchaseTime() != null) {
                invoiceResponse.setPurchaseTime(DateUtils.instantToString(i.getPurchaseTime()));
            }

            List<Evaluation> evaluations = evaluationRepository.findAllByUser_IdAndInvoice_Id(userId, i.getId());
            invoiceResponse.setIsEvaluated(!evaluations.isEmpty());

            invoiceResponses.add(invoiceResponse);
        });
        return invoiceResponses;
    }


    public InvoiceResponseDTO getById(int id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy hóa đơn"));

        InvoiceResponseDTO invoiceResponse = new InvoiceResponseDTO();
        invoiceResponse.setId(invoice.getId());
        invoiceResponse.setEvent(eventMapper.toEventResponseDTO(invoice.getEvent()));
        invoiceResponse.setUser(userService.convertToUserDTO(invoice.getUser().getId()));

        if (invoiceResponse.getVoucher() != null) {
            VoucherResponseDTO voucherResponse = voucherMapper.toVoucherDTO(invoice.getVoucher());
            voucherResponse.setEffectiveDate(DateUtils.instantToString(invoice.getVoucher().getEffectiveDate()));
            voucherResponse.setExpirationDate(DateUtils.instantToString(invoice.getVoucher().getExpirationDate()));
            invoiceResponse.setVoucher(voucherResponse);
        }


        invoiceResponse.setTickets(ticketService.getTicketOfInvoice(invoice.getId()));

        invoiceResponse.setMerchandises(merchandiseService.getMerchandisesOfInvoice(invoice.getId()));

        invoiceResponse.setStatus(invoice.getStatus());
        invoiceResponse.setTotalPayment(invoice.getTotalPayment());
        if (invoice.getPurchaseTime() != null) {
            invoiceResponse.setPurchaseTime(DateUtils.instantToString(invoice.getPurchaseTime()));
        }
        return invoiceResponse;
    }


    public List<PaymentDateStat> getUserStatsForMonth (int userId, int year) {
        return invoiceRepository.getUserStatsForMonth(userId, year);
    }

    public List<PaymentDateStat> getUserStatsForYear (int userId) {
        return invoiceRepository.getUserStatsForYear(userId);
    }


    public List<TicketInfoByEventDTO> getTicketInfoByEvent (int eventId) {
        return invoiceRepository.getTicketInfoByEvent(eventId);
    }

}
