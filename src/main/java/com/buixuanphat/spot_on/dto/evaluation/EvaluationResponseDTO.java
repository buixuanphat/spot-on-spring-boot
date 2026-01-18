package com.buixuanphat.spot_on.dto.evaluation;

import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluationResponseDTO {

    Integer id;
    UserPublicInfoResponseDTO user;
    Integer eventId;
    Integer invoiceId;
    Integer rating;
    String content;
    String createdDate;
}
