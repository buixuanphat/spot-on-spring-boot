package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.evaluation.CreateEvaluationDTO;
import com.buixuanphat.spot_on.dto.evaluation.EvaluationResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import com.buixuanphat.spot_on.entity.*;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.repository.EvaluationRepository;
import com.buixuanphat.spot_on.repository.EventRepository;
import com.buixuanphat.spot_on.repository.InvoiceRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EvaluationService {

    EvaluationRepository evaluationRepository;
    UserRepository userRepository;
    EventRepository eventRepository;
    UserService userService;
    InvoiceRepository invoiceRepository;

    public EvaluationResponseDTO create(CreateEvaluationDTO request)
    {
        Evaluation evaluation = new Evaluation();

        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        Event event = eventRepository.findById(request.getEventId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));

        evaluation.setUser(user);
        evaluation.setEvent(event);
        evaluation.setContent(request.getContent());
        evaluation.setRating(request.getRating());
        evaluation.setCreatedDate(Instant.now());
        evaluation.setInvoice(invoice);

        user.setCoins(user.getCoins() + 1000);
        userRepository.save(user);

        return convert(evaluationRepository.save(evaluation));
    }


    public Page<EvaluationResponseDTO> getEvaluations(int eventId, int page, int pageSize)
    {
        Specification<Evaluation> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("event").get("id"), eventId));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Evaluation> evaluations = evaluationRepository.findAll(specification ,pageable);
        return evaluations.map(this::convert);
    }



    EvaluationResponseDTO convert(Evaluation evaluation)
    {
        EvaluationResponseDTO response = new EvaluationResponseDTO();
        response.setId(evaluation.getId());
        response.setContent(evaluation.getContent());
        response.setRating(evaluation.getRating());
        response.setCreatedDate(DateUtils.instantToString(evaluation.getCreatedDate()));
        response.setEventId(evaluation.getEvent().getId());
        response.setUser(userService.convertToUserDTO(evaluation.getUser().getId()));
        response.setInvoiceId(evaluation.getInvoice().getId());
        return response;
    }


}
