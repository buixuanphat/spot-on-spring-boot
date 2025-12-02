package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class UserOrganizerService {

    UserOrganizerRepository  userOrganizerRepository;

    OrganizerRepository organizerRepository;

    OrganizerMapper  organizerMapper;

    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_organizer')")
    public OrganizerResponseDTO getOrganizer (int userId)
    {
        UserOrganizer userOrganizer = userOrganizerRepository.findById(userId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        Organizer organizer = organizerRepository.findById(userOrganizer.getOrganizer().getId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy ban tổ chức"));

        OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(organizer);
        response.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));
        return response;
    }


}
