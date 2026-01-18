package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.genre.GenreResponseDTO;
import com.buixuanphat.spot_on.dto.event.CreateEventDTO;
import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.stats.AdminPaymentStat;
import com.buixuanphat.spot_on.dto.stats.AdminTicketStat;
import com.buixuanphat.spot_on.entity.*;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.mapper.GenreMapper;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.GenreRepository;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.SectionRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableScheduling
@SpringBootApplication
public class EventService {

    EventRepository eventRepository;

    OrganizerRepository organizerRepository;

    CloudinaryService cloudinaryService;

    EventMapper eventMapper;

    OrganizerMapper organizerMapper;

    GenreRepository genreRepository;
    GenreMapper genreMapper;

    SectionRepository sectionRepository;

    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public EventResponseDTO register(CreateEventDTO request) {
        Map<String, String> uploadImage = null;
        Map<String, String> uploadLicense = null;

        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Ban tổ chức không tồn tại"));

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Thể loại không tồn tại"));

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
                .date(DateUtils.stringtoLocalDate(request.getDate()))
                .startTime(DateUtils.stringToLocalTime(request.getStartTime()))
                .endTime(DateUtils.stringToLocalTime(request.getEndTime()))
                .image(uploadImage.get("url"))
                .imageId(uploadImage.get("id"))
                .license(uploadLicense.get("url"))
                .licenseId(uploadLicense.get("id"))
                .organizer(organizer)
                .genre(genre)
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



    public Page<EventResponseDTO> getEvents ( Integer organizerId ,Integer id, String name ,String status, String genre, String province ,int page, int size)
    {
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
                if(status.equals("public")) {
                    predicates.add(cb.lower(root.get("status")).in(Status.expired.name().toLowerCase(), Status.running.name().toLowerCase()));
                }
                else
                {
                    predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase() ));
                }
            }
            if(genre!=null)
            {
                predicates.add(cb.like(root.get("genre").get("name"), "%"+genre+"%"));
            }
            if(province!=null)
            {
                predicates.add(cb.like(root.get("province"), "%"+province+"%"));
            }
            predicates.add(cb.equal(root.get("active"), true));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return eventRepository.findAll(specification, pageable).map(e ->
        {
            EventResponseDTO response = eventMapper.toEventResponseDTO(e);
            toResponse(response, e);
            return response;
        });
    }


    @Scheduled(fixedRate = 60 * 1000)
    public void updateExpiredEvent() {
        LocalDateTime now = LocalDateTime.now();

        List<Event> events = eventRepository.findALlByStatus(Status.running.name());
        events.forEach(e -> {
            LocalDateTime expiredTime =
                    LocalDateTime.of(e.getDate(), e.getEndTime());

            if (now.isAfter(expiredTime)) {
                e.setStatus(Status.expired.name());
            }
        });

        eventRepository.saveAll(events);
    }


    public EventResponseDTO run (int id) {
        Event event = eventRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));
        List<Section> sections = sectionRepository.findAllByEvent_Id(id);
        if(sections.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Vui lòng tạo loại vé trước khi mở bán");
        }
        event.setStatus(Status.running.name());
        Event saved = eventRepository.save(event);
        EventResponseDTO response = eventMapper.toEventResponseDTO(saved);
        toResponse(response, event);
        return response;
    }



    public List<AdminPaymentStat> getEventPaymentStat(int month , int year)
    {
        return eventRepository.getEventPaymentStat(month, year);
    }


    public List<AdminTicketStat> getEventTicketStat(int month , int year)
    {
        return eventRepository.getEventTicketStat(month, year);
    }

    public List<EventResponseDTO> getNew()
    {
        List<Event> events = eventRepository.findTop10ByOrderByIdDesc();
        return events.stream().map(e ->
        {
            EventResponseDTO response = eventMapper.toEventResponseDTO(e);
            toResponse(response, e);
            return response;
        }).toList();
    }

    public List<EventResponseDTO> getTopSale()
    {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        List<Event> events = eventRepository.getTopSale(month, year);
        return events.stream().map(e ->
        {
            EventResponseDTO response = eventMapper.toEventResponseDTO(e);
            toResponse(response, e);
            return response;
        }).toList();
    }




    public void toResponse(EventResponseDTO response, Event event) {
        response.setDate(DateUtils.localDateToString(event.getDate()));
        response.setStartTime(DateUtils.localTimeToString(event.getStartTime()));
        response.setEndTime(DateUtils.localTimeToString(event.getEndTime()));
        response.setCreatedDate(DateUtils.instantToString(event.getCreatedDate()));

        OrganizerResponseDTO organizer = organizerMapper.toOrganizerResponseDTO(event.getOrganizer());
        organizer.setCreatedDate(DateUtils.instantToString(event.getOrganizer().getCreatedDate()));

        GenreResponseDTO genre = genreMapper.toGenreResponseDTO(event.getGenre());

        response.setOrganizer(organizer);
        response.setGenre(genre);
    }

}
