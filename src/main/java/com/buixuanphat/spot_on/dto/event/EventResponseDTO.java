package com.buixuanphat.spot_on.dto.event;

import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

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

}
