package com.buixuanphat.spot_on.dto.organizer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrganizerResponseDTO {

    Integer id;
    String taxCode;
    String bankNumber;
    String bank;
    String email;
    String phoneNumber;
    String address;
    String description;
    String createdDate;
    Boolean active;
    String status;
    String name;
    String avatar;
    String businessLicense;
}
