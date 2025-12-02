package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.organizer.OrganizerCreateRequestDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.user.CreateOrganizerUserRequestDTO;
import com.buixuanphat.spot_on.dto.user.CreateStaffRequestDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.enums.Role;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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


    public OrganizerResponseDTO register(OrganizerCreateRequestDTO request) {
        if (organizerRepository.existsByEmail(request.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Email đã được sử dụng");
        }

        if (organizerRepository.existsByBankNumberAndBank(request.getBankNumber(), request.getBank())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Tài khoản ngân hàng đã được sử dụng");
        }

        if (organizerRepository.existsByTaxCode(request.getTaxCode())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Mã số thuế đã tồn tại");
        }

        if (organizerRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Tên đã được sử dụng");
        }
        if (organizerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Số điện thoại đã được sử dụng");
        }

        Map<String, String> uploadAvatar = cloudinaryService.uploadImage(request.getAvatar());
        Map<String, String> uploadLicense = cloudinaryService.uploadFile(request.getLicense());

        Organizer organizer = Organizer.builder()
                .name(request.getName())
                .taxCode(request.getTaxCode())
                .bankNumber(request.getBankNumber())
                .bank(request.getBank())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .description(request.getDescription())
                .createdDate(Instant.now())
                .active(true)
                .status(Status.pending.name())
                .avatar(uploadAvatar.get("url"))
                .avatarId(uploadAvatar.get("id"))
                .businessLicense(uploadLicense.get("url"))
                .businessLicenseId(uploadLicense.get("id"))
                .build();

        OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(organizerRepository.save(organizer));
        response.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));
        return response;
    }


    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public Page<OrganizerResponseDTO> getOrganizers(Integer id ,String name, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Organizer> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if(id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
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

    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_organizer')")
    public OrganizerResponseDTO getOrganizer(int id)
    {
        Organizer organizer = organizerRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy ban tổ chức"));
        OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(organizer);
        response.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));
        return response;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_staff')")
    public UserResponseDTO verify(int organizerId, boolean accept) {
        Organizer organizer = organizerRepository.findById(organizerId).orElseThrow(()->{
            return new AppException(HttpStatus.NOT_FOUND.value(), "Ban tổ chức không tồn tại");
        });

        if (accept) {
            organizer.setStatus(Status.verified.name());
            organizerRepository.save(organizer);

            CreateOrganizerUserRequestDTO request = CreateOrganizerUserRequestDTO.builder()
                    .email(organizer.getEmail())
                    .password(organizer.getTaxCode())
                    .organizerAvatar(organizer.getAvatar())
                    .organizerId(organizer.getId())
                    .build();
            UserResponseDTO response = userService.createOrganizerUser(request);
            System.err.println(response);

            UserOrganizer userOrganizer = new UserOrganizer();
            userOrganizer.setUser(userRepository.findById(response.getId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Người dùng không tồn tại")));
            userOrganizer.setOrganizer(organizer);

            return response;
        }


        else {
            organizer.setStatus(Status.rejected.name());
            organizerRepository.save(organizer);
            return new UserResponseDTO();
        }
    }


}
