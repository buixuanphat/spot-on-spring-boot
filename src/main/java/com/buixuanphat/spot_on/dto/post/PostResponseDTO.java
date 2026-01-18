package com.buixuanphat.spot_on.dto.post;

import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.Post;
import com.buixuanphat.spot_on.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponseDTO {

    Integer id;
    UserPublicInfoResponseDTO user;
    EventResponseDTO event;
    String caption;
    String createdDate;
    Integer emotions;
    Integer comments;
    List<String> images;
    Boolean isLiked;
}
