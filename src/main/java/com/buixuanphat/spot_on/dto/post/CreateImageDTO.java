package com.buixuanphat.spot_on.dto.post;

import com.buixuanphat.spot_on.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateImageDTO {

    int postId;
    MultipartFile image;

}
