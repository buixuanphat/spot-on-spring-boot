package com.buixuanphat.spot_on.dto.ticket;

import com.buixuanphat.spot_on.dto.invoice.InvoiceResponseDTO;
import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponseDTO {


    Integer id;
    SectionResponseDTO section;
    String createdDate;
    String status;
    Integer invoiceId;

}
