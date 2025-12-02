package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {

    EventRepository eventRepository;

    OrganizerRepository organizerRepository;

    CloudinaryService cloudinaryService;

    EventMapper eventMapper;


    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public EventResponseDTO register(CreateEventDTO request) {

        Map<String, String> uploadImage = null;
        Map<String, String> uploadLicense = null;

        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Ban tổ chức không tồn tại"));

        uploadImage = cloudinaryService.uploadImage(request.getImage());
        uploadLicense = cloudinaryService.uploadFile(request.getLicense());

        Event event = Event.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .ageLimit(request.getAgeLimit())
                .active(false)
                .status(Status.pending.name())
                .createdDate(Instant.now())
                .startTime(DateUtils.stringToInstant(request.getStartTime()))
                .endTime(DateUtils.stringToInstant(request.getEndTime()))
                .image(uploadImage.get("url"))
                .imageId(uploadImage.get("id"))
                .license(uploadLicense.get("url"))
                .licenseId(uploadLicense.get("id"))
                .organizer(organizer)
                .build();


        EventResponseDTO response = eventMapper.toEventResponseDTO(eventRepository.save(event));
        toResponse(response, event);

        return response;

    }


    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_staff')")
    public EventResponseDTO verify(int eventId, boolean accept) {
        Event event;
        try {
            event = eventRepository.getReferenceById(eventId);
        } catch (Exception e) {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Sự kiện không tồn tại");
        }
        if (accept) {
            event.setStatus(Status.verified.name());
        } else {
            event.setStatus(Status.rejected.name());
        }
        EventResponseDTO response = eventMapper.toEventResponseDTO(eventRepository.save(event));
        toResponse(response, event);

        return response;
    }



    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_staff', 'SCOPE_organizer')")
    public Page<EventResponseDTO> getEvents (Integer id, String name, String status ,Boolean active ,int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Event> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if(id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase() ));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventRepository.findAll(specification, pageable).map(e ->
        {
            EventResponseDTO response = eventMapper.toEventResponseDTO(e);
            response.setCreatedDate(DateUtils.instantToString(e.getCreatedDate()));
            response.setStartTime(DateUtils.instantToString(e.getStartTime()));
            response.setEndTime(DateUtils.instantToString(e.getEndTime()));
            response.setOrganizerId(e.getOrganizer().getId());
            return  response;
        });
    }


    void toResponse(EventResponseDTO response, Event event) {
        response.setStartTime(DateUtils.instantToString(event.getStartTime()));
        response.setEndTime(DateUtils.instantToString(event.getEndTime()));
        response.setCreatedDate(DateUtils.instantToString(event.getCreatedDate()));;
        response.setOrganizerId(event.getOrganizer().getId());
    }

}
