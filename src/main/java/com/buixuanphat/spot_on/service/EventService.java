package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.section.CreateSectionDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.enums.OrganizerStatus;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.exception.ErrorMessage;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.time.Instant;
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

    SectionService sectionService;

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public EventResponseDTO createEvent(String requestStr,
                                        MultipartFile image,
                                        MultipartFile license,
                                        String sectionsStr) {

        Gson gson = new Gson();
        CreateEventDTO request;

        Map<String, String> uploadImage = null;
        Map<String, String> uploadLicense = null;

        try {
            request = gson.fromJson(requestStr, CreateEventDTO.class);

            uploadImage = cloudinaryService.uploadImage(image);
            uploadLicense = cloudinaryService.uploadLicense(license);

            Event event = Event.builder()
                    .name(request.getName())
                    .location(request.getLocation())
                    .description(request.getDescription())
                    .ageLimit(request.getAgeLimit())
                    .active(true)
                    .status(OrganizerStatus.pending.name())
                    .createdDate(Instant.now())
                    .startTime(DateUtils.stringToInstant(request.getStartTime()))
                    .endTime(DateUtils.stringToInstant(request.getEndTime()))
                    .image(uploadImage.get("url"))
                    .imageId(uploadImage.get("id"))
                    .license(uploadLicense.get("url"))
                    .licenseId(uploadLicense.get("id"))
                    .build();

            Organizer org = organizerRepository.findById(request.getOrganizerId())
                    .orElseThrow(() -> new AppException(ErrorMessage.ORGANIZER_NOT_FOUND));

            event.setOrganizer(org);

            Event saved = eventRepository.save(event);

            Type listType = new TypeToken<List<CreateSectionDTO>>(){}.getType();
            List<CreateSectionDTO> sections = gson.fromJson(sectionsStr, listType);

            for (CreateSectionDTO s : sections) {
                s.setEventId(saved.getId());
                sectionService.createSection(s);
            }

            EventResponseDTO response = eventMapper.toEventResponseDTO(saved);
            toResponse(response, saved);

            return response;

        } catch (Exception e) {

            if (uploadImage != null) cloudinaryService.deleteImage(uploadImage.get("id"));
            if (uploadLicense != null) cloudinaryService.deleteFile(uploadLicense.get("id"));

            throw new RuntimeException(e);
        }
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_staff')")
    public EventResponseDTO verify(int eventId, boolean accept)
    {
        Event event;
        try
        {
            event = eventRepository.getReferenceById(eventId);
        }
        catch (Exception e)
        {
            throw new AppException(ErrorMessage.EVENT_NOT_FOUND);
        }
        if (accept) {
            event.setStatus(OrganizerStatus.verified.name());
        }
        else
        {
            event.setStatus(OrganizerStatus.rejected.name());
        }
        EventResponseDTO response = eventMapper.toEventResponseDTO(eventRepository.save(event));
        toResponse(response, event);

        return response;
    }


    void toResponse(EventResponseDTO response, Event event)
    {
        response.setStartTime(DateUtils.instantToString(event.getStartTime()));
        response.setEndTime(DateUtils.instantToString(event.getEndTime()));
        response.setCreatedDate(DateUtils.instantToString(event.getCreatedDate()));
        response.setOrganizerId(event.getOrganizer().getId());
    }

}
