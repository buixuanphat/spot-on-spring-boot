package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.entity.Merchandise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchandiseMapper {

    @Mapping(target = "organizer", ignore = true)
    MerchandiseResponseDTO toMerchandiseResponseDTO(Merchandise merchandise);
}
