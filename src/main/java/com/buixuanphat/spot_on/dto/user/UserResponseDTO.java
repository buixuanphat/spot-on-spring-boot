package com.buixuanphat.spot_on.dto.user;

import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {
    Integer id;
    String firstname;
    String lastname;
    String email;
    String avatar;
    Integer coins;
    String tier;
    String role;
    String createdDate;
    Boolean active;
    OrganizerResponseDTO organizer;
}
