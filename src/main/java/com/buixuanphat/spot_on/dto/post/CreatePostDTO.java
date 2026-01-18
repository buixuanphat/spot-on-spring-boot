package com.buixuanphat.spot_on.dto.post;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePostDTO {


    int userId;
    Integer eventId;
    @Size(max = 200, message = "Nội dung không được quá 200 từ")
    @Size(min = 1, message = "Nội dung không được để trống")
    String caption;

}
