package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.user.CreateCustomerRequestDTO;
import com.buixuanphat.spot_on.dto.user.CreateUserRequestDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.enums.Role;
import com.buixuanphat.spot_on.enums.Tier;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.exception.ErrorMessage;
import com.buixuanphat.spot_on.mapper.UserMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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


    public UserResponseDTO createCustomer(CreateCustomerRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorMessage.USER_EXISTED);
        }

        User user = userMapper.toUser(request);

        user.setDateOfBirth(DateUtils.stringtoLocalDate(request.getDateOfBirth()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRole(Role.CUSTOMER.getName());
        user.setTier(Tier.copper.name());
        user.setCoins(0);

        UserResponseDTO response = userMapper.toUserResponseDTO(userRepository.save(user));
        response.setDateOfBirth(DateUtils.localDateToString(user.getDateOfBirth()));

        return response;
    }


    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public Page<UserResponseDTO> getUsers(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        if (email != null) {
            users = userRepository.findByEmailContainingIgnoreCaseAndActive(email, true, pageable);
        } else {
            users = userRepository.findByActive(true, pageable);
        }

        return users.map(userMapper::toUserResponseDTO);
    }


    public UserResponseDTO updateAvatar(int userId, MultipartFile avatar) {

        User user;
        try {
            user = userRepository.getReferenceById(userId);

        } catch (EntityNotFoundException e) {
            throw new AppException(ErrorMessage.USER_NOT_FOUND);
        }
        if (user.getAvatarId() != null) {
            cloudinaryService.deleteImage(user.getAvatarId());
        }

        Map<String, String> response = cloudinaryService.uploadAvatar(avatar);
        user.setAvatar(response.get("url"));
        user.setAvatarId(response.get("id"));
        return userMapper.toUserResponseDTO(userRepository.save(user));
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_staff')")
    public UserResponseDTO createUser(CreateUserRequestDTO request, @Nullable MultipartFile file) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorMessage.USER_EXISTED);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRole(request.getRole());

        if (file != null) {
            Map<String, String> uploadResponse = cloudinaryService.uploadAvatar(file);
            user.setAvatar(uploadResponse.get("url"));
            user.setAvatarId(uploadResponse.get("id"));
        } else {
            user.setAvatar(request.getAvatar());
        }

        User newUser = userRepository.save(user);

        // if this user is an agent of organizer, create relationship
        if (request.getOrganizerId() != null && request.getRole().equals(Role.ORGANIZER.getName())) {
            UserOrganizer userOrganizer = new UserOrganizer();
            userOrganizer.setUser(newUser);
            userOrganizer.setOrganizer(organizerRepository.getReferenceById(request.getOrganizerId()));
            userOrganizerRepository.save(userOrganizer);
        }

        return userMapper.toUserResponseDTO(newUser);
    }


}
