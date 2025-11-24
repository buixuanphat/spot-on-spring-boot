package com.buixuanphat.spot_on.dto.merchandise;

import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MerchandiseResponseDTO {

    Integer id;
    String name;
    String image;
    double price;
    OrganizerResponseDTO organizer;

}
