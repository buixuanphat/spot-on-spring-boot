package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.user.CreateCustomerRequestDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toUser(CreateCustomerRequestDTO createCustomerRequestDTO);

    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    UserResponseDTO toUserResponseDTO(User user);
}
