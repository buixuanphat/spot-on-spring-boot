package com.buixuanphat.spot_on.dto.event;

import com.buixuanphat.spot_on.dto.genre.GenreResponseDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponseDTO {

    Integer id;
    String name;
    String startTime;
    String endTime;
    String date;
    String address;
    String province;
    String district;
    String ward;
    String description;
    String image;
    OrganizerResponseDTO organizer;
    Integer ageLimit;
    String createdDate;
    Boolean active;
    String status;
    String license;
    GenreResponseDTO genre;
}
