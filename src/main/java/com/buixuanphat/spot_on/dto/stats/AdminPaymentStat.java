package com.buixuanphat.spot_on.dto.stats;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminPaymentStat {
    Integer id;
    String name;
    BigDecimal total;
}
