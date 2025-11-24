package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.organizer.OrganizerCreateRequestDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizerMapper {

    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "businessLicense", ignore = true)
    public Organizer toOrganizer(OrganizerCreateRequestDTO request);

    @Mapping(target = "createdDate", ignore = true)
    public OrganizerResponseDTO toOrganizerResponseDTO(Organizer organizer);
}
