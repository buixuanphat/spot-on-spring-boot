package com.buixuanphat.spot_on.dto.event;

import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.enums.OrganizerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEventDTO {

    @Size(min = 3, max = 100, message = "NAME_INVALID")
    String name;
    String startTime;
    String endTime;
    @Size(min = 3, max = 100, message = "ADDRESS_INVALID")
    String location;
    @Size(min = 3, max = 100, message = "DESCRIPTION_INVALID")
    String description;
    Integer organizerId;
    @Min(value = 0, message = "AGE_LIMIT_INVALID")
    @Max(value = 18, message = "AGE_LIMIT_INVALID")
    Integer ageLimit;

}
