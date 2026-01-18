package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.merchandise.CreateMerchandiseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.entity.InvoiceMerchandise;
import com.buixuanphat.spot_on.entity.Merchandise;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.MerchandiseMapper;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.InvoiceMerchandiseRepository;
import com.buixuanphat.spot_on.repository.MerchandiseRepository;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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

    InvoiceMerchandiseRepository invoiceMerchandiseRepository;


    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public MerchandiseResponseDTO create(CreateMerchandiseDTO request) {
        Merchandise merchandise = Merchandise.builder()
                .name(request.getName())
                .price(request.getPrice())
                .organizer(organizerRepository.getReferenceById(request.getOrganizerId()))
                .build();

        Map<String, String> uploadImage = cloudinaryService.uploadImage(request.getImage());
        merchandise.setImage(uploadImage.get("url"));
        merchandise.setImageId(uploadImage.get("id"));

        MerchandiseResponseDTO response = merchandiseMapper.toMerchandiseResponseDTO(merchandiseRepository.save(merchandise));
        response.setOrganizer(organizerMapper.toOrganizerResponseDTO(merchandise.getOrganizer()));

        return response;
    }


    @PreAuthorize("hasAuthority('SCOPE_organizer')")
    public MerchandiseResponseDTO update(int id ,CreateMerchandiseDTO request) {

        Merchandise merchandise = merchandiseRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đồ lưu niệm"));

        merchandise.setName(request.getName());
        merchandise.setPrice(request.getPrice());

        if(request.getImage()!=null)
        {
            Map<String, String> uploadImage = cloudinaryService.uploadImage(request.getImage());
            merchandise.setImage(uploadImage.get("url"));
            merchandise.setImageId(uploadImage.get("id"));
        }

        MerchandiseResponseDTO response = merchandiseMapper.toMerchandiseResponseDTO(merchandiseRepository.save(merchandise));
        response.setOrganizer(organizerMapper.toOrganizerResponseDTO(merchandise.getOrganizer()));

        return response;
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_organizer')")
    public Page<MerchandiseResponseDTO> getMerchandises(@Nullable Integer id,
                                                        @Nullable String name,
                                                        @Nullable Integer organizerId,
                                                        int page, int size) {
        Specification<Merchandise> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if(organizerId != null){
                Join<Merchandise, Organizer> organizerJoin = root.join("organizer");
                predicates.add(cb.equal(organizerJoin.get("id"), organizerId));
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


    public MerchandiseResponseDTO getMerchandise(int id)
    {
        Merchandise merchandise = merchandiseRepository.findById(id)
                .orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đồ lưu niệm"));

        Organizer organizer = merchandise.getOrganizer();
        OrganizerResponseDTO organizerResponse = organizerMapper.toOrganizerResponseDTO(organizer);
        organizerResponse.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));

        MerchandiseResponseDTO response = merchandiseMapper.toMerchandiseResponseDTO(merchandise);
        response.setOrganizer(organizerResponse);

        return response;
    }


    public List<MerchandiseResponseDTO> getMerchandisesOfInvoice(int invoiceId)
    {
        List<InvoiceMerchandise> invoiceMerchandises = invoiceMerchandiseRepository.findAllByInvoice_Id(invoiceId);

        List<MerchandiseResponseDTO> responses = new ArrayList<>();

        invoiceMerchandises.forEach(m -> {
            responses.add(merchandiseMapper.toMerchandiseResponseDTO(m.getMerchandise()));
        });

        return responses;
    }

}
