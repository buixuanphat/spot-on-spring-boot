package com.buixuanphat.spot_on.dto.invoice;

import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.dto.ticket.TicketResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponseDTO {

    Integer id;
    UserPublicInfoResponseDTO user;
    VoucherResponseDTO voucher;
    String purchaseTime;
    String status;
    Double totalPayment;
    List<TicketResponseDTO> tickets;
    List<MerchandiseResponseDTO> merchandises;
    EventResponseDTO event;
    Boolean isEvaluated;
    Integer coins;
}
