package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    TicketResponseDTO toTicketResponseDTO(Ticket ticket);
}
