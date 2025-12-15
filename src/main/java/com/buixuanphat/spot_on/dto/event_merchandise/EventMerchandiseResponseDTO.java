package com.buixuanphat.spot_on.dto.event_merchandise;

import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventMerchandiseResponseDTO {
    int id;
    MerchandiseResponseDTO merchandise;
}
