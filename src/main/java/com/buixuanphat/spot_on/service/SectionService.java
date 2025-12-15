package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.section.CreateSectionDTO;
import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.entity.Section;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.SectionMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.SectionRepository;
import jakarta.validation.constraints.Min;
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
public class SectionService {

    SectionRepository sectionRepository;

    EventRepository eventRepository;

    SectionMapper sectionMapper;

    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public SectionResponseDTO createSection(CreateSectionDTO request)
    {
        Section section = Section.builder()
                .limitTicket(request.getLimitTicket())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .color(request.getColor())
                .totalSeats(request.getTotalSeats())
                .build();

        section.setEvent(eventRepository.findById(request.getEventId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện")));

        SectionResponseDTO response = sectionMapper.toSectionResponseDTO(sectionRepository.save(section));
        response.setEventId(section.getEvent().getId());
        return response;
    }



    public List<SectionResponseDTO> getSections (Integer eventId)
    {
        List<Section> sections = sectionRepository.findAllByEvent_Id(eventId);

        return sections.stream().map(s ->
        {
            SectionResponseDTO response = sectionMapper.toSectionResponseDTO(s);
            response.setEventId(s.getEvent().getId());
            return response;
        }).toList();
    }


    public String delete (int id)
    {
        sectionRepository.deleteById(id);
        return "Đã xóa thành công";
    }


}
