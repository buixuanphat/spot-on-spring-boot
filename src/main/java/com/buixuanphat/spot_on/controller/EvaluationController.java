package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.evaluation.CreateEvaluationDTO;
import com.buixuanphat.spot_on.dto.evaluation.EvaluationResponseDTO;
import com.buixuanphat.spot_on.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EvaluationController {

    @NonFinal
    @Value("${pagination.page-size}")
    int size;

    EvaluationService evaluationService;

    @PostMapping("/evaluations")
    ApiResponse<EvaluationResponseDTO> create (@Valid @RequestBody CreateEvaluationDTO request)
    {
        return ApiResponse.<EvaluationResponseDTO>builder().data(evaluationService.create(request)).build();
    }


    @GetMapping("events/{id}/evaluations")
    ApiResponse<Page<EvaluationResponseDTO>> getByEvent (@PathVariable int id ,@RequestParam(defaultValue = "0") Integer page)
    {
        return ApiResponse.<Page<EvaluationResponseDTO>>builder().data(evaluationService.getEvaluations(id, page, size)).build();
    }


}
