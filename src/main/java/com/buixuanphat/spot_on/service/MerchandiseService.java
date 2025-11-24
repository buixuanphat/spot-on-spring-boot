package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.merchandise.CreateMerchandiseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.entity.Merchandise;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.mapper.MerchandiseMapper;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.MerchandiseRepository;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MerchandiseService {

    MerchandiseRepository merchandiseRepository;

    OrganizerRepository organizerRepository;

    CloudinaryService cloudinaryService;

    MerchandiseMapper merchandiseMapper;

    OrganizerMapper organizerMapper;


    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public MerchandiseResponseDTO create(CreateMerchandiseDTO request) {
        Merchandise merchandise = Merchandise.builder()
                .name(request.getName())
                .price(request.getPrice())
                .organizer(organizerRepository.getReferenceById(request.getOrganizerId()))
                .build();

        Map<String, String> uploadImage = cloudinaryService.uploadImage(request.getImage());
        merchandise.setImage(uploadImage.get("image"));
        merchandise.setImageId(uploadImage.get("id"));

        MerchandiseResponseDTO response = merchandiseMapper.toMerchandiseResponseDTO(merchandiseRepository.save(merchandise));
        response.setOrganizer(organizerMapper.toOrganizerResponseDTO(merchandise.getOrganizer()));

        return response;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_organizer')")
    public Page<MerchandiseResponseDTO> getMerchandises(@Nullable Integer organizerId,
                                                        @Nullable String name,
                                                        int page, int size) {
        Specification<Merchandise> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (organizerId != null) {
                Join<Merchandise ,Organizer> organizerJoin = root.join("organizer");
                predicates.add(cb.equal(organizerJoin.get("organizer").get("id"), organizerId));
            }
            if (name != null) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };

        Pageable pageable = PageRequest.of(page, size);

        return merchandiseRepository.findAll(specification, pageable).map(m ->
        {
            MerchandiseResponseDTO response = merchandiseMapper.toMerchandiseResponseDTO(m);
            response.setOrganizer(organizerMapper.toOrganizerResponseDTO(m.getOrganizer()));
            return response;
        });
    }
}
