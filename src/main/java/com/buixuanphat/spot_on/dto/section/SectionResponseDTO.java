package com.buixuanphat.spot_on.dto.section;

import com.buixuanphat.spot_on.entity.Event;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectionResponseDTO {

    Integer id;
    Integer limitTicket;
    String name;
    String description;
    Double price;
    String color;
    Integer totalSeats;
    Integer eventId;

}
