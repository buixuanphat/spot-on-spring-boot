package com.buixuanphat.spot_on.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEventDTO {

    @Size(min = 3, max = 100, message = "Tên sự kiện phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    String name;
    String startTime;
    String endTime;
    String date;
    @Size(min = 3, max = 100, message = "Địa chỉ phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    String address;
    String province;
    String district;
    String ward;
    String description;
    Integer organizerId;
    @Min(value = 6, message = "Độ tuổi giới hạn phải từ 6 đến 18 tuổi")
    @Max(value = 18, message = "Độ tuổi giới hạn phải từ 6 đến 18 tuổi")
    Integer ageLimit;
    MultipartFile image;
    MultipartFile license;
    int genreId;

}
