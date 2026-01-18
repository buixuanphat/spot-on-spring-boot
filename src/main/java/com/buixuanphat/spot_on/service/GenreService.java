package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.genre.GenreResponseDTO;
import com.buixuanphat.spot_on.repository.GenreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenreService {
    GenreRepository genreRepository;

    public List<GenreResponseDTO> getGenres() {
        List<GenreResponseDTO> genres = genreRepository.findAll().stream().map(g->
        {
            return GenreResponseDTO.builder().id(g.getId()).name(g.getName()).build();
        }).toList();
        return genres;
    }
}
