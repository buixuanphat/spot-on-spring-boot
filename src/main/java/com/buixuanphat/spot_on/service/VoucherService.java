package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.voucher.CreateVoucherDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.Voucher;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.VoucherMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.VoucherRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherService {

    VoucherRepository voucherRepository;

    OrganizerRepository organizerRepository;

    VoucherMapper voucherMapper;

    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public VoucherResponseDTO create(CreateVoucherDTO request) {
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Mã đã tồn tại");
        }

        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .organizer(organizerRepository.findById(request.getOrganizerId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Ban tổ chức không tồn tại")))
                .effectiveDate(DateUtils.stringToInstant(request.getEffectiveDate()))
                .expirationDate(DateUtils.stringToInstant(request.getExpirationDate()))
                .limitUsed(request.getLimitUsed())
                .type(request.getType())
                .value(request.getValue())
                .build();

        VoucherResponseDTO response = voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
        response.setEffectiveDate(DateUtils.instantToString(voucher.getEffectiveDate()));
        response.setExpirationDate(DateUtils.instantToString(voucher.getExpirationDate()));
        response.setOrganizerId(voucher.getOrganizer().getId());
        return response;
    }


    public List<VoucherResponseDTO> getVouchersByOrganizer(int organizerId, String code) {
        List<Voucher> vouchers;
        if (code != null) {
            vouchers = voucherRepository.findAllByCodeContainingIgnoreCaseAndOrganizer_Id(code, organizerId).orElseThrow(
                    () -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy voucher"));
        } else {
            vouchers = voucherRepository.findAllByOrganizer_Id(organizerId).orElseThrow(
                    () -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy voucher"));
        }


        return vouchers.stream().map(v ->
        {
            VoucherResponseDTO response = voucherMapper.toVoucherDTO(v);
            response.setEffectiveDate(DateUtils.instantToString(v.getEffectiveDate()));
            response.setExpirationDate(DateUtils.instantToString(v.getExpirationDate()));
            return response;
        }).toList();

    }


    public String delete(int id) {
        voucherRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));
        voucherRepository.deleteById(id);
        return "Đã xóa thành công";
    }

}
