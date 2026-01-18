package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.Genre;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.entity.UserGenre;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.GenreRepository;
import com.buixuanphat.spot_on.repository.UserGenreRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserGenreService {

    UserGenreRepository userGenreRepository;

    UserRepository userRepository;

    GenreRepository genreRepository;

    EventRepository eventRepository;

    EventService eventService;
    EventMapper eventMapper;

    public Void interactive(int eventId, int userId, String action) {

        Event event = eventRepository.findById(eventId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));
        Genre genre = event.getGenre();
        User user = userRepository.findById(userId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dung"));
        UserGenre userGenre = userGenreRepository.findByUser_IdAndGenre_Id(userId, genre.getId()).orElse(UserGenre.builder().build());


        if (userGenre.getValue() != null) {
            System.err.println(userGenre.getValue());
            if(action.equals("watch")) {
                userGenre.setValue(userGenre.getValue()+1);
            }
            else if(action.equals("buy")) {
                userGenre.setValue(userGenre.getValue()+10);
            }
            userGenreRepository.save(userGenre);
        }
        else
        {
            userGenre.setUser(user);
            userGenre.setGenre(genre);
            if(action.equals("watch")) {
                userGenre.setValue(1);
            }
            else if(action.equals("buy")) {
                userGenre.setValue(10);
            }
            userGenreRepository.save(userGenre);
        }
        return null;
    }

    public List<EventResponseDTO> getRecomment(int userId)
    {
        List<Event> events = userGenreRepository.getRecomment(userId);

        return events.stream().map(e ->
        {
            EventResponseDTO response = eventMapper.toEventResponseDTO(e);
            eventService.toResponse(response, e);
            return response;
        }).toList();
    }

}
