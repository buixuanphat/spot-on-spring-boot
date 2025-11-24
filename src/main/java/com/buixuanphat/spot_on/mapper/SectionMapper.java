package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.entity.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    SectionResponseDTO toSectionResponseDTO(Section section);
}
