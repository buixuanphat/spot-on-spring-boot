package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.event_merchandise.EventMerchandiseResponseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.EventMerchandise;
import com.buixuanphat.spot_on.entity.Merchandise;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.MerchandiseMapper;
import com.buixuanphat.spot_on.repository.EventMerchandiseRepository;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.MerchandiseRepository;
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
public class EventMerchandiseService {

    EventMerchandiseRepository eventMerchandiseRepository;

    EventRepository eventRepository;

    MerchandiseRepository merchandiseRepository;
    MerchandiseMapper merchandiseMapper;

    public List<EventMerchandiseResponseDTO> getMerchandiseByEvent(int eventId) {
        List<EventMerchandise> eventMerchandises = eventMerchandiseRepository.findAllByEvent_Id(eventId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đồ lưu niệm của sự kiện này"));

        return eventMerchandises.stream().map((em->{

            EventMerchandiseResponseDTO response = new EventMerchandiseResponseDTO();
            response.setId(em.getId());

            MerchandiseResponseDTO merchandiseResponse = merchandiseMapper.toMerchandiseResponseDTO(em.getMerchandise());

            response.setMerchandise(merchandiseResponse);

            return response;

        })).toList();
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_organizer')")
    public Map<String, String> createEventMerchandise(Map<String, String> request)
    {
        Merchandise merchandise = merchandiseRepository.findById( Integer.parseInt(request.get("merchandiseId"))).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy ban tổ chức"));
        Event event = eventRepository.findById( Integer.parseInt(request.get("eventId"))).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));

        EventMerchandise eventMerchandise = new EventMerchandise();
        eventMerchandise.setEvent(event);
        eventMerchandise.setMerchandise(merchandise);

        EventMerchandise saved = eventMerchandiseRepository.save(eventMerchandise);

        Map<String, String> response = new HashMap<>();
        response.put("merchandiseId", String.valueOf(saved.getMerchandise().getId()));
        response.put("eventId", String.valueOf(saved.getEvent().getId()));
        return response;

    }

    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_organizer')")
    public String deleteEventMerchandise(int id)
    {
        EventMerchandise eventMerchandise = eventMerchandiseRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NO_CONTENT.value(), "Không tìm thấy đồ lưu niệm của sự kiện này"));
        eventMerchandiseRepository.delete(eventMerchandise);
        return "Xóa thành công";
    }


}
