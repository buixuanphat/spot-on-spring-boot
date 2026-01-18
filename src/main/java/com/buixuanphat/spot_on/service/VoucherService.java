package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.voucher.CreateVoucherDTO;
import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.entity.Invoice;
import com.buixuanphat.spot_on.entity.Voucher;
import com.buixuanphat.spot_on.entity.VoucherEvent;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.VoucherMapper;
import com.buixuanphat.spot_on.repository.InvoiceRepository;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.VoucherEventRepository;
import com.buixuanphat.spot_on.repository.VoucherRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherService {

    VoucherRepository voucherRepository;

    OrganizerRepository organizerRepository;

    VoucherMapper voucherMapper;

    VoucherEventRepository voucherEventRepository;

    InvoiceRepository invoiceRepository;

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

        if(request.getTier()!=null)
        {
            voucher.setTier(request.getTier());
        }

        VoucherResponseDTO response = voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
        response.setTier(voucher.getTier());
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
            response.setTier(v.getTier());
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


    public VoucherResponseDTO getVoucher(int id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));

        VoucherResponseDTO response = voucherMapper.toVoucherDTO(voucher);
        response.setEffectiveDate(DateUtils.instantToString(voucher.getEffectiveDate()));
        response.setExpirationDate(DateUtils.instantToString(voucher.getExpirationDate()));
        response.setOrganizerId(voucher.getOrganizer().getId());
        response.setTier(voucher.getTier());
        return response;
    }


    public VoucherResponseDTO getVoucherByCode(int userId ,String code, int eventId) {
        VoucherEvent voucherEvent = voucherEventRepository.findByEvent_IdAndVoucher_Code(eventId, code);
        if(voucherEvent==null) throw new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá");

        //Kiểm tra thời hạn sử dụng
        Instant now = Instant.now();
        if(now.isBefore(voucherEvent.getVoucher().getEffectiveDate()) || now.isAfter(voucherEvent.getVoucher().getExpirationDate()))
        {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Mã giảm giá đã hết hạn");
        }

        //Kiểm tra đã sử dụng chưa
        Invoice invoice = invoiceRepository.findAllByUser_IdAndVoucher_Id(userId, voucherEvent.getVoucher().getId());
        if(invoice!=null) throw new AppException(HttpStatus.NOT_FOUND.value(), "Mã giảm giá đã được sử dụng");

        //Kiểm tra số lượt sử dụng
        List<Invoice> invoices = invoiceRepository.findAllByVoucher_Id(voucherEvent.getVoucher().getId());
        if(invoices.size()>=voucherEvent.getVoucher().getLimitUsed())
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Mã giảm giá đã hết lượt sử dụng");

        VoucherResponseDTO response = voucherMapper.toVoucherDTO(voucherEvent.getVoucher());
        response.setEffectiveDate(DateUtils.instantToString(voucherEvent.getVoucher().getEffectiveDate()));
        response.setExpirationDate(DateUtils.instantToString(voucherEvent.getVoucher().getExpirationDate()));
        response.setOrganizerId(voucherEvent.getVoucher().getOrganizer().getId());
        response.setTier(voucherEvent.getVoucher().getTier());
        return response;
    }



    public VoucherResponseDTO update (int id, CreateVoucherDTO request) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));

        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setEffectiveDate(DateUtils.stringToInstant(request.getEffectiveDate()));
        voucher.setExpirationDate(DateUtils.stringToInstant(request.getExpirationDate()));
        voucher.setLimitUsed(request.getLimitUsed());
        voucher.setType(request.getType());
        voucher.setValue(request.getValue());
        if(request.getTier()!=null)
        {
            voucher.setTier(request.getTier());
        }

        VoucherResponseDTO response = voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
        response.setEffectiveDate(DateUtils.instantToString(voucher.getEffectiveDate()));
        response.setExpirationDate(DateUtils.instantToString(voucher.getExpirationDate()));
        response.setOrganizerId(voucher.getOrganizer().getId());
        response.setTier(voucher.getTier());
        return response;
    }

}
