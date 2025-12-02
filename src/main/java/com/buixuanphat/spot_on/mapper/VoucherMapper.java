package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface VoucherMapper {
    @Mapping(target = "effectiveDate", ignore = true)
    @Mapping(target = "expirationDate", ignore = true)
    VoucherResponseDTO toVoucherDTO(Voucher voucher);
}
