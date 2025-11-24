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

    private Integer id;
    private String name;
    private String startTime;
    private String endTime;
    private String location;
    private String description;
    private String image;
    private Integer organizerId;
    private Integer ageLimit;
    private String createdDate;
    private Boolean active;
    private String status;
    private String license;

}
