package com.buixuanphat.spot_on.dto.comment;

import com.buixuanphat.spot_on.entity.Comment;
import com.buixuanphat.spot_on.entity.Post;
import com.buixuanphat.spot_on.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCommentDTO {
    int userId;
    int postId;
    @Size(min = 1, message = "Nội dung không được để trống")
    @Size(max = 100, message = "Nội dung không được quá 100 kí tự")
    String content;
    Integer parentId;
}
