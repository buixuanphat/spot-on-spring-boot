package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.section.CreateSectionDTO;
import com.buixuanphat.spot_on.dto.section.SectionResponseDTO;
import com.buixuanphat.spot_on.entity.Section;
import com.buixuanphat.spot_on.mapper.SectionMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.SectionRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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

        section.setEvent(eventRepository.getReferenceById(request.getEventId()));
        return sectionMapper.toSectionResponseDTO(sectionRepository.save(section));
    }




}
