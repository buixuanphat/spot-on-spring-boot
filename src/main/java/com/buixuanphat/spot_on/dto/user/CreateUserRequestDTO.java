package com.buixuanphat.spot_on.dto.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ Builder
public class CreateUserRequestDTO {
    @Email(message = "EMAIL_INVALID")
    String email;
    @Size(min = 8, max = 16, message = "PASSWORD_INVALID")
    String password;
    String avatar;
    Integer organizerId;
    String role;
}
