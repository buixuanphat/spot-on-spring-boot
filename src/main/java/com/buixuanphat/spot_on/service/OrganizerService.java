package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.organizer.OrganizerCreateRequestDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.user.CreateUserRequestDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.enums.OrganizerStatus;
import com.buixuanphat.spot_on.enums.Role;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.exception.ErrorMessage;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor()
public class OrganizerService {

    OrganizerRepository organizerRepository;

    OrganizerMapper organizerMapper;

    UserRepository userRepository;

    UserService userService;

    CloudinaryService cloudinaryService;

    UserOrganizerRepository userOrganizerRepository;


    public OrganizerResponseDTO createOrganizer(OrganizerCreateRequestDTO request, MultipartFile avatar, MultipartFile businessLicense) {
        if (organizerRepository.existsByEmail(request.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorMessage.USER_EXISTED);
        }

        if (organizerRepository.existsByBankNumberAndBank(request.getBankNumber(), request.getBank())) {
            throw new AppException(ErrorMessage.BANK_USED);
        }

        if (organizerRepository.existsByTaxCode(request.getTaxCode())) {
            throw new AppException(ErrorMessage.TAX_CODE_USED);
        }

        if (organizerRepository.existsByName(request.getName())) {
            throw new AppException(ErrorMessage.NAME_USED);
        }
        if (organizerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorMessage.PHONE_NUMBER_USED);
        }

        Map<String, String> avatarResponse = cloudinaryService.uploadAvatar(avatar);
        Map<String, String> businessLicenseResponse = cloudinaryService.uploadLicense(businessLicense);

        Organizer organizer = organizerMapper.toOrganizer(request);
        organizer.setCreatedDate(Instant.now());
        organizer.setActive(true);
        organizer.setStatus(OrganizerStatus.pending.name());
        organizer.setAvatar(avatarResponse.get("url"));
        organizer.setAvatarId(avatarResponse.get("id"));
        organizer.setBusinessLicense(businessLicenseResponse.get("url"));
        organizer.setBusinessLicenseId(businessLicenseResponse.get("id"));
        return organizerMapper.toOrganizerResponseDTO(organizerRepository.save(organizer));
    }


    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public Page<OrganizerResponseDTO> getOrganizers(String name, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Organizer> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            predicates.add(cb.equal(root.get("active"), true));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return organizerRepository.findAll(specification, pageable).map(o ->
        {
            OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(o);
            response.setCreatedDate(DateUtils.instantToString(o.getCreatedDate()));
            return  response;
        });
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_staff')")
    public UserResponseDTO verify(int organizerId, boolean accept) {
        Organizer organizer = organizerRepository.getReferenceById(organizerId);

        // update status = verified and create an agent
        if (accept) {
            organizer.setStatus(OrganizerStatus.verified.name());
            organizerRepository.save(organizer);
            log.error(organizer.getTaxCode());
            CreateUserRequestDTO user = CreateUserRequestDTO.builder()
                    .email(organizer.getEmail())
                    .password(organizer.getTaxCode())
                    .avatar(organizer.getAvatar())
                    .role(Role.ORGANIZER.getName())
                    .build();
            UserResponseDTO newUser = userService.createUser(user, null);

            UserOrganizer userOrganizer = new UserOrganizer();
            userOrganizer.setUser(userRepository.getReferenceById(newUser.getId()));
            userOrganizer.setOrganizer(organizer);
            userOrganizerRepository.save(userOrganizer);

            return newUser;
        }

        // just update status = rejected
        else {
            organizer.setStatus(OrganizerStatus.rejected.name());
            organizerRepository.save(organizer);
            return new UserResponseDTO();
        }
    }


}
