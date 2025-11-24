package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.authentication.AuthenticationRequestDTO;
import com.buixuanphat.spot_on.dto.authentication.AuthenticationResponseDTO;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.exception.ErrorMessage;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
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

    @NonFinal
    @Value("${jwt.signerKey}")
    String signerKey;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        User u = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            return new AppException(ErrorMessage.USER_NOT_FOUND);
        });

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), u.getPassword());

        if (!isAuthenticated) {
            throw new AppException(ErrorMessage.WRONG_PASSWORD);
        } else {
            return AuthenticationResponseDTO.builder()
                    .isAuthenticated(true)
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
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
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
