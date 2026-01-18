package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.genre.GenreResponseDTO;
import com.buixuanphat.spot_on.service.GenreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenreController {
    GenreService genreService;

    @GetMapping("/genres")
    ApiResponse<List<GenreResponseDTO>> getAllGenres() {
        return ApiResponse.<List<GenreResponseDTO>>builder()
                .data(genreService.getGenres())
                .build();
    }

}
