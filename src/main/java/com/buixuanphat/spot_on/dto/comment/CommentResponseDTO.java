package com.buixuanphat.spot_on.dto.comment;

import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDTO {

    Integer id;
    UserPublicInfoResponseDTO user;
    int postId;
    String content;
    String createdDate;
    Integer parentId;
    Integer childrens;
}
