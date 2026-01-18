package com.buixuanphat.spot_on.dto.voucher;

import com.buixuanphat.spot_on.entity.Organizer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponseDTO {
    Integer id;
    String code;
    String description;
    String effectiveDate;
    String expirationDate;
    Integer organizerId;
    Integer limitUsed;
    String type;
    Double value;
    String tier;
}
