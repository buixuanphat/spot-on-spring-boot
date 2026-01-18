package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.dto.ticket.CreateTicketRequestDTO;
import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.entity.Section;
import com.buixuanphat.spot_on.entity.Ticket;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.SectionMapper;
import com.buixuanphat.spot_on.mapper.TicketMapper;
import com.buixuanphat.spot_on.repository.InvoiceRepository;
import com.buixuanphat.spot_on.repository.SectionRepository;
import com.buixuanphat.spot_on.repository.TicketRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {

    TicketRepository ticketRepository;
    TicketMapper  ticketMapper;

    SectionRepository sectionRepository;
    SectionMapper sectionMapper;

    InvoiceRepository invoiceRepository;

    public List<TicketResponseDTO> getTicketOfInvoice(int invoiceId)
    {
        List<Ticket> tickets = ticketRepository.findAllByInvoice_Id(invoiceId);
        List<TicketResponseDTO> ticketResponses = new ArrayList<>();
        tickets.forEach(t -> {
            TicketResponseDTO response = ticketMapper.toTicketResponseDTO(t);
            response.setCreatedDate(DateUtils.instantToString(t.getCreatedDate()));
            response.setSection(sectionMapper.toSectionResponseDTO(t.getSection()));
            response.setInvoiceId(t.getInvoice().getId());
            ticketResponses.add(response);
        });
        return ticketResponses;
    }


    public Boolean check(int id)
    {
        Ticket ticket =  ticketRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy vé"));
        if(!Objects.equals(ticket.getStatus(), "available"))
        {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Vé đã được sử dụng");
        }
        ticket.setStatus("used");
        ticketRepository.save(ticket);
        return true;
    }



}
