package com.buixuanphat.spot_on.dto.user;

import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPublicInfoResponseDTO {
    Integer id;
    String firstname;
    String lastname;
    String avatar;
    String role;
    String createdDate;
    Boolean active;
}
