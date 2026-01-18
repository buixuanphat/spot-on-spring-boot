package com.buixuanphat.spot_on.dto.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TicketInfoByEventDTO {
    Integer id;
    String firstname;
    String lastname;
    String email;
    String section_name;
    String status;
}
