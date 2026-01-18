package com.buixuanphat.spot_on.mapper;

import com.buixuanphat.spot_on.dto.genre.GenreResponseDTO;
import com.buixuanphat.spot_on.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponseDTO toGenreResponseDTO(Genre genre);
}
