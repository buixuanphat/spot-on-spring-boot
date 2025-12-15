package com.buixuanphat.spot_on.dto.ticket;

import com.buixuanphat.spot_on.entity.Invoice;
import com.buixuanphat.spot_on.entity.Section;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTicketRequestDTO {


    int sectionId;

    //Invoice invoiceId;
    //Instant createdDate;
    //String status;

}
