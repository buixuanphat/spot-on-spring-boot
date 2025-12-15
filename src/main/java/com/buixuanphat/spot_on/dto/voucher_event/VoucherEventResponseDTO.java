package com.buixuanphat.spot_on.dto.voucher_event;

import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherEventResponseDTO {
    int id;
    VoucherResponseDTO voucher;
    int eventId;
}
