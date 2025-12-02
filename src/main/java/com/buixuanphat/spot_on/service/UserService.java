package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.user.*;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.enums.Role;
import com.buixuanphat.spot_on.enums.Tier;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.UserMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.http.HttpStatus;
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
public class UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    CloudinaryService cloudinaryService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    OrganizerRepository organizerRepository;

    UserOrganizerRepository userOrganizerRepository;


    public UserResponseDTO register(CreateCustomerRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Email đã được sử dụng");
        }

        User user = userMapper.toUser(request);
        user.setDateOfBirth(DateUtils.stringtoLocalDate(request.getDateOfBirth()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setCreatedDate(Instant.now());
        user.setRole(Role.customer.name());
        user.setTier(Tier.copper.name());
        user.setCoins(0);

        UserResponseDTO response = userMapper.toUserResponseDTO(userRepository.save(user));
        response.setDateOfBirth(DateUtils.localDateToString(user.getDateOfBirth()));
        response.setCreatedDate(DateUtils.localDateToString(user.getDateOfBirth()));

        return response;
    }


    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public Page<UserResponseDTO> getUsers(Integer id, String email, int page, int size) {

        Specification<User> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id ));
            }
            if (email != null) {
                predicates.add(cb.like(root.get("email"), "%" +email + "%"));
            }
            predicates.add(cb.equal(root.get("active"), true));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll(specification, pageable).map(u ->
        {
            UserResponseDTO response = userMapper.toUserResponseDTO(u);
            response.setDateOfBirth(DateUtils.localDateToString(u.getDateOfBirth()));
            response.setCreatedDate(DateUtils.instantToString(u.getCreatedDate()));
            return response;
        });
    }

    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public UserResponseDTO getUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy thông tin người dùng"));
        UserResponseDTO response = userMapper.toUserResponseDTO(user);
        response.setDateOfBirth(DateUtils.localDateToString(user.getDateOfBirth()));
        response.setCreatedDate(DateUtils.instantToString(user.getCreatedDate()));
        return response;
    }


    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public UserResponseDTO createStaff(CreateStaffRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Email đã được sử dụng");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(Role.staff.name())
                .createdDate(Instant.now())
                .build();

        Map<String, String> uploadAvatar = cloudinaryService.uploadImage(request.getAvatar());
        user.setAvatar(uploadAvatar.get("url"));
        user.setAvatarId(uploadAvatar.get("id"));

        UserResponseDTO response = userMapper.toUserResponseDTO(userRepository.save(user));
        response.setCreatedDate(DateUtils.instantToString(user.getCreatedDate()));
        return response;
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('SCOPE_admin','SCOPE_staff')")
    public UserResponseDTO createOrganizerUser(CreateOrganizerUserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Email đã được sử dụng");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(Role.organizer.name())
                .createdDate(Instant.now())
                .build();

        if (request.getOrganizerAvatar() != null) {
            user.setAvatar(request.getOrganizerAvatar());
        } else {
            Map<String, String> uploadAvatar = cloudinaryService.uploadImage(request.getAvatar());
            user.setAvatar(uploadAvatar.get("url"));
            user.setAvatarId(uploadAvatar.get("id"));
        }

        UserResponseDTO response = userMapper.toUserResponseDTO(userRepository.save(user));
        response.setCreatedDate(DateUtils.instantToString(user.getCreatedDate()));

        UserOrganizer userOrganizer = UserOrganizer.builder()
                .user(user)
                .organizer(organizerRepository.getReferenceById(request.getOrganizerId()))
                .build();
        userOrganizerRepository.save(userOrganizer);

        return response;
    }


    public UserResponseDTO update ( int id ,UpdateUserRequestDTO request)
    {
        User user = userRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Người dùng không tồn tại"));

        if(request.getFirstname() != null) user.setFirstname(request.getFirstname());
        if(request.getLastname() != null) user.setLastname(request.getLastname());
        if(request.getEmail() != null) user.setEmail(request.getEmail());
        if(request.getPassword() != null) user.setPassword(request.getPassword());
        if(request.getRole() != null) user.setRole(request.getRole());
        if(request.getDateOfBirth() != null) user.setDateOfBirth(DateUtils.stringtoLocalDate(request.getDateOfBirth()));
        if(request.getCoins() != null) user.setCoins(request.getCoins());
        if(request.getTier() != null) user.setTier(request.getTier());
        if(request.getActive() != null) user.setActive(request.getActive());
        if(request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if(request.getAvatar() != null)
        {
            cloudinaryService.deleteImage(user.getAvatarId());
            Map<String, String> upload = cloudinaryService.uploadImage(request.getAvatar());
            user.setAvatar(upload.get("url"));
            user.setAvatarId(upload.get("id"));
        }
        UserResponseDTO response = userMapper.toUserResponseDTO(userRepository.save(user));
        response.setCreatedDate(DateUtils.instantToString(user.getCreatedDate()));
        response.setDateOfBirth(DateUtils.localDateToString(user.getDateOfBirth()));
        return response;
    }

}
