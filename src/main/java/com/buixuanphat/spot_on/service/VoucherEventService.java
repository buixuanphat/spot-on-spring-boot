package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.voucher.VoucherResponseDTO;
import com.buixuanphat.spot_on.dto.voucher_event.VoucherEventResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.Voucher;
import com.buixuanphat.spot_on.entity.VoucherEvent;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.VoucherMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.VoucherEventRepository;
import com.buixuanphat.spot_on.repository.VoucherRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherEventService {

    VoucherEventRepository voucherEventRepository;

    VoucherRepository voucherRepository;

    EventRepository eventRepository;

    VoucherMapper voucherMapper;


    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public Map<String, Integer> createVoucherEvent(Map<String, Integer> request) {
        Voucher voucher = voucherRepository.findById(request.get("voucherId")).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));
        Event event = eventRepository.findById(request.get("eventId")).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));

        VoucherEvent voucherEvent = VoucherEvent.builder().voucher(voucher).event(event).build();
        VoucherEvent voucherEventSaved = voucherEventRepository.save(voucherEvent);

        Map<String, Integer> response = new HashMap<>();
        response.put("id", voucherEventSaved.getId());
        response.put("voucherId", voucherEventSaved.getVoucher().getId());
        response.put("eventId", voucherEventSaved.getEvent().getId());

        return response;
    }

    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public String delete(int id) {
        voucherEventRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá của sự kiện"));
        voucherEventRepository.deleteById(id);
        return "Đã xóa thành công";


    }


    public List<VoucherEventResponseDTO> getVouchersByEvent(int eventId) {
        List<VoucherEvent> voucherEvents = voucherEventRepository.findAllByEvent_Id(eventId);
        return voucherEvents.stream().map(ve ->
        {
            Voucher voucher = voucherRepository.findById(ve.getVoucher().getId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy mã giảm giá"));
            VoucherResponseDTO voucherResponse = voucherMapper.toVoucherDTO(voucher);
            voucherResponse.setEffectiveDate(DateUtils.instantToString(voucher.getEffectiveDate()));
            voucherResponse.setExpirationDate(DateUtils.instantToString(voucher.getExpirationDate()));

            VoucherEventResponseDTO response = new VoucherEventResponseDTO();
            response.setId(ve.getId());
            response.setVoucher(voucherResponse);
            response.setEventId(ve.getEvent().getId());

            return response;
        }).toList();
    }

}
