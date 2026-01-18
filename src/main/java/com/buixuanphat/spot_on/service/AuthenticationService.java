package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.authentication.AuthenticationRequestDTO;
import com.buixuanphat.spot_on.dto.authentication.AuthenticationResponseDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.enums.Role;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.mapper.UserMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    UserMapper userMapper;

    OrganizerRepository organizerRepository;
    OrganizerMapper organizerMapper;

    UserOrganizerRepository userOrganizerRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    String signerKey;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponseDTO getCurrentUser ()
    {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user;
        user = userRepository.findByEmail(String.valueOf(auth.getName())).orElseThrow(()->new AppException(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value(), "Non-Authoritative Information"));
        if(user != null)
        {
            UserResponseDTO response = userMapper.toUserResponseDTO(user);
            response.setCreatedDate(DateUtils.instantToString(user.getCreatedDate()));

            if(response.getRole().equalsIgnoreCase(Role.organizer.name()))
            {
                UserOrganizer userOrganizer = userOrganizerRepository.findByUser_Id(response.getId()).orElseThrow(
                        ()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy thôn tin ban tổ chức"));
                OrganizerResponseDTO organizer = organizerMapper.toOrganizerResponseDTO(userOrganizer.getOrganizer());
                organizer.setCreatedDate(DateUtils.instantToString(userOrganizer.getOrganizer().getCreatedDate()));

                response.setOrganizer(organizer);
            }

            return response;
        }
        return new UserResponseDTO();
    }


    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        User u = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            return new AppException(HttpStatus.NOT_FOUND.value(), "Tài khoàn không tồn tại");
        });

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), u.getPassword());

        if (!isAuthenticated) {
            throw new AppException(HttpStatus.UNAUTHORIZED.value(), "Mật khẩu không chính xác" );
        } else {
            return AuthenticationResponseDTO.builder()
                    .role(u.getRole())
                    .token(generateToken(u))
                    .build();
        }
    }

    private String generateToken(User u) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(u.getEmail())
                .issuer("spot_on_system")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(100, ChronoUnit.DAYS).toEpochMilli()
                ))
                .claim("scope", u.getRole())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Không thể tạo token", e);
            throw new RuntimeException(e);
        }
    }

}
