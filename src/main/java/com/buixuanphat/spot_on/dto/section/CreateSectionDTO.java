package com.buixuanphat.spot_on.dto.section;

import com.buixuanphat.spot_on.entity.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateSectionDTO {

    @Min(value = 1, message = "TICKET_LIMIT_INVALID")
    private Integer limitTicket;
    @Size(min = 3, max = 100, message = "NAME_INVALID")
    private String name;
    @Size(min = 3, max = 100, message = "DESCRIPTION_INVALID")
    private String description;
    private Double price;
    private String color;
    private Integer totalSeats;
    private Integer eventId;

}
