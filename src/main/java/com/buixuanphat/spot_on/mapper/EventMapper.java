package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    EventResponseDTO toEventResponseDTO(Event event);
}
