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

    @Min(value = 1, message = "Giới hạn số lượng vé phải lớn hơn 1")
    private Integer limitTicket;
    @Size(min = 3, max = 100, message = "Tên vé phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    private String name;
    @Size(min = 3, max = 500, message = "Mô tả phải lớn hơn 3 kí tự và bé hơn 500 kí tự")
    private String description;
    private Double price;
    private String color;
    private Integer totalSeats;
    private Integer eventId;

}
