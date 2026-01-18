package com.buixuanphat.spot_on.dto.evaluation;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEvaluationDTO {
    int userId;
    int eventId;
    int invoiceId;
    Integer rating;
    @Size(min = 3, max = 100, message = "Nội dung phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    String content;
}
