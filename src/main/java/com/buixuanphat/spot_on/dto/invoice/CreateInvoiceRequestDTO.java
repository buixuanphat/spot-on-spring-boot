package com.buixuanphat.spot_on.dto.invoice;

import com.buixuanphat.spot_on.dto.merchandise.CreateMerchandiseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.dto.ticket.CreateTicketRequestDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.entity.Voucher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInvoiceRequestDTO {


    int userId;
    Integer voucherId;
    List<CreateTicketRequestDTO> tickets;
    Double totalPayment;
    List<MerchandiseResponseDTO> merchandises;
    int eventId;
    Integer coins;

    //double totalPayment;
    //Instant purchaseTime;
    //String status;
}
