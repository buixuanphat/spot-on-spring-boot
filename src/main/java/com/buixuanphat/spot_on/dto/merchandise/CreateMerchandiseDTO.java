package com.buixuanphat.spot_on.dto.merchandise;

import com.buixuanphat.spot_on.entity.Organizer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateMerchandiseDTO {

    @Size(min = 3, max = 100, message = "Tên vật phẩm phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    String name;
    Double price;
    MultipartFile image;
    Integer organizerId;
}
