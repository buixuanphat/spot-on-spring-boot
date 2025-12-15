package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.ticket.CreateTicketRequestDTO;
import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.entity.Section;
import com.buixuanphat.spot_on.entity.Ticket;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.repository.SectionRepository;
import com.buixuanphat.spot_on.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketService {


}
