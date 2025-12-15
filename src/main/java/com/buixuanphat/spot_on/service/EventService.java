package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
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

    OrganizerMapper organizerMapper;


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
                .address(request.getAddress())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .description(request.getDescription())
                .ageLimit(request.getAgeLimit())
                .active(true)
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
        Event event = eventRepository.findById(eventId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));
        if (accept) {
            event.setStatus(Status.verified.name());
        } else {
            event.setStatus(Status.rejected.name());
        }
        EventResponseDTO response = eventMapper.toEventResponseDTO(eventRepository.save(event));
        toResponse(response, event);

        return response;
    }

    public EventResponseDTO getEvent(int id)
    {
        Event event = eventRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));

        EventResponseDTO response = eventMapper.toEventResponseDTO(event);
        toResponse(response, event);
        return response;
    }



    public Page<EventResponseDTO> getEvents ( Integer organizerId ,Integer id, String name, String status ,int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Event> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if(organizerId != null)
            {
                predicates.add(cb.equal(root.get("organizer").get("id"), organizerId));
            }
            if(id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase() ));
            }
            predicates.add(cb.equal(root.get("active"), true));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventRepository.findAll(specification, pageable).map(e ->
        {
            EventResponseDTO response = eventMapper.toEventResponseDTO(e);
            toResponse(response, e);
            return response;
        });
    }


    void toResponse(EventResponseDTO response, Event event) {
        response.setStartTime(DateUtils.instantToString(event.getStartTime()));
        response.setEndTime(DateUtils.instantToString(event.getEndTime()));
        response.setCreatedDate(DateUtils.instantToString(event.getCreatedDate()));

        OrganizerResponseDTO organizer = organizerMapper.toOrganizerResponseDTO(event.getOrganizer());
        organizer.setCreatedDate(DateUtils.instantToString(event.getOrganizer().getCreatedDate()));

        response.setOrganizer(organizer);
    }

}
