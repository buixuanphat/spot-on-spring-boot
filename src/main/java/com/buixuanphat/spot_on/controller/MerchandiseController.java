package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.merchandise.CreateMerchandiseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.repository.MerchandiseRepository;
import com.buixuanphat.spot_on.service.MerchandiseService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MerchandiseController {

    MerchandiseService  merchandiseService;

    @NonFinal
    @Value("${pagination.page-size}")
    int size;

    @PostMapping("/merchandises")
    ApiResponse<MerchandiseResponseDTO> create(@Valid @ModelAttribute CreateMerchandiseDTO request){
        return ApiResponse.<MerchandiseResponseDTO>builder()
                .data(merchandiseService.create(request))
                .build();
    }

    @GetMapping("/merchandises")
    ApiResponse<Page<MerchandiseResponseDTO>> getMerchandises(@RequestParam @Nullable Integer organizerId,
                                                              @RequestParam @Nullable String name,
                                                              @RequestParam (defaultValue = "0") int page)
    {
        return ApiResponse.<Page<MerchandiseResponseDTO>>builder()
                .data(merchandiseService.getMerchandises(organizerId, name, page, size))
                .build();
    }


}
