package com.buixuanphat.spot_on.dto.voucher;

import com.buixuanphat.spot_on.entity.Organizer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVoucherDTO {
    @Size(min = 6, max = 20, message = "Mã phải có độ dài từ 6 đến 20 kí tự")
    String code;
    @Size(min = 3, max = 100, message = "Mô tả phải có độ dài từ 6 đến 10 kí tự")
    String description;
    String effectiveDate;
    String expirationDate;
    Integer organizerId;
    Integer limitUsed;
    String type;
    Double value;
    String tier;
}
