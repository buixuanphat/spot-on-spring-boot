package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.organizer.OrganizerCreateRequestDTO;
import com.buixuanphat.spot_on.dto.organizer.OrganizerResponseDTO;
import com.buixuanphat.spot_on.dto.stats.AdminPaymentStat;
import com.buixuanphat.spot_on.dto.stats.AdminTicketStat;
import com.buixuanphat.spot_on.dto.user.CreateOrganizerUserRequestDTO;
import com.buixuanphat.spot_on.dto.user.UserResponseDTO;
import com.buixuanphat.spot_on.entity.Organizer;
import com.buixuanphat.spot_on.entity.UserOrganizer;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.OrganizerMapper;
import com.buixuanphat.spot_on.repository.OrganizerRepository;
import com.buixuanphat.spot_on.repository.UserOrganizerRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor()
public class OrganizerService {

    OrganizerRepository organizerRepository;

    OrganizerMapper organizerMapper;

    UserRepository userRepository;

    UserService userService;

    CloudinaryService cloudinaryService;

    UserOrganizerRepository userOrganizerRepository;

    EmailService emailService;


    public OrganizerResponseDTO register(OrganizerCreateRequestDTO request) {
        if (organizerRepository.existsByEmail(request.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Email đã được sử dụng");
        }

        if (organizerRepository.existsByBankNumberAndBank(request.getBankNumber(), request.getBank())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Tài khoản ngân hàng đã được sử dụng");
        }

        if (organizerRepository.existsByTaxCode(request.getTaxCode())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Mã số thuế đã tồn tại");
        }

        if (organizerRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Tên đã được sử dụng");
        }
        if (organizerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Số điện thoại đã được sử dụng");
        }

        Map<String, String> uploadAvatar = cloudinaryService.uploadImage(request.getAvatar());
        Map<String, String> uploadLicense = cloudinaryService.uploadFile(request.getLicense());

        Organizer organizer = Organizer.builder()
                .name(request.getName())
                .taxCode(request.getTaxCode())
                .bankNumber(request.getBankNumber())
                .bank(request.getBank())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .description(request.getDescription())
                .createdDate(Instant.now())
                .active(true)
                .status(Status.pending.name())
                .avatar(uploadAvatar.get("url"))
                .avatarId(uploadAvatar.get("id"))
                .businessLicense(uploadLicense.get("url"))
                .businessLicenseId(uploadLicense.get("id"))
                .build();

        OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(organizerRepository.save(organizer));
        response.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));

        return response;
    }


    public Page<OrganizerResponseDTO> getOrganizers(Integer id ,String name, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Organizer> specification = (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if(id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            predicates.add(cb.equal(root.get("active"), true));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return organizerRepository.findAll(specification, pageable).map(o ->
        {
            OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(o);
            response.setCreatedDate(DateUtils.instantToString(o.getCreatedDate()));
            return  response;
        });
    }


    public OrganizerResponseDTO getOrganizer(int id)
    {
        Organizer organizer = organizerRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy ban tổ chức"));
        OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(organizer);
        response.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));
        return response;
    }

    @Transactional
    public UserResponseDTO verify(int organizerId, boolean accept) {
        Organizer organizer = organizerRepository.findById(organizerId).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Ban tổ chức không tồn tại"));

        if (accept) {
            organizer.setStatus(Status.verified.name());
            organizerRepository.save(organizer);

            CreateOrganizerUserRequestDTO request = CreateOrganizerUserRequestDTO.builder()
                    .email(organizer.getEmail())
                    .password(organizer.getTaxCode())
                    .organizerAvatar(organizer.getAvatar())
                    .organizerId(organizer.getId())
                    .build();
            UserResponseDTO response = userService.createOrganizerUser(request);

            UserOrganizer userOrganizer = new UserOrganizer();
            userOrganizer.setUser(userRepository.findById(response.getId()).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Người dùng không tồn tại")));
            userOrganizer.setOrganizer(organizer);

            // Gửi mail gồm email đăng nhập và mật khẩu
            String to = organizer.getEmail();
            String subject = "SPOTON - THÔNG BÁO PHÊ DUYỆT TÀI KHOẢN BAN TỔ CHỨC";

            String text = String.format(
                    "Kính gửi Quý đối tác,\n\n" +
                            "Yêu cầu tạo tài khoản Ban tổ chức của bạn đã được phê duyệt thành công.\n" +
                            "Vui lòng đăng nhập hệ thống với thông tin sau:\n" +
                            "- Email đăng nhập: %s\n" +
                            "- Mật khẩu : %s\n\n" +
                            "Trân trọng,\n" +
                            "Đội ngũ hỗ trợ hệ thống.",
                    organizer.getEmail(),
                    organizer.getTaxCode()
            );

            emailService.sendSimpleMail(to, subject, text);

            return response;
        }


        else {
            // Gửi mail thông báo từ chối
            String to = organizer.getEmail();
            String subject = "SPOTON - THÔNG BÁO KẾT QUẢ PHÊ DUYỆT TÀI KHOẢN";

            String text = String.format(
                    "Kính gửi Quý đối tác,\n\n" +
                            "Cảm ơn bạn đã đăng ký tham gia vào hệ thống.\n" +
                            "Chúng tôi rất tiếc phải thông báo rằng yêu cầu tạo tài khoản Ban tổ chức của bạn đã bị TỪ CHỐI sau quá trình kiểm duyệt hồ sơ.\n\n" +
                            "Trân trọng,\n" +
                            "Đội ngũ hỗ trợ hệ thống."
            );

            emailService.sendSimpleMail(to, subject, text);

            organizer.setStatus(Status.rejected.name());
            organizerRepository.save(organizer);
            return new UserResponseDTO();
        }
    }


    public List<AdminPaymentStat> getOrganizerPaymentStat(int month , int year)
    {
        return organizerRepository.getOrganizerPaymentStat(month, year);
    }


    public List<AdminTicketStat> getOrganizerTicketStat(int month , int year)
    {
        return organizerRepository.getOrganizerTicketStat(month, year);
    }


    public OrganizerResponseDTO updateOrganizer(int id ,OrganizerCreateRequestDTO request)
    {
        Organizer organizer = organizerRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy ban tổ chức"));

        organizer.setName(request.getName());
        organizer.setEmail(request.getEmail());
        organizer.setTaxCode(request.getTaxCode());
        organizer.setPhoneNumber(request.getPhoneNumber());
        organizer.setBank(request.getBank());
        organizer.setBankNumber(request.getBankNumber());
        organizer.setAddress(request.getAddress());
        organizer.setDescription(request.getDescription());
        organizer.setStatus(request.getStatus());


        Map<String, String> uploadAvatar;
        if(request.getAvatar() != null)
        {
            uploadAvatar = cloudinaryService.uploadImage(request.getAvatar());
            organizer.setAvatar(uploadAvatar.get("url"));
            organizer.setAvatarId(uploadAvatar.get("id"));
        }

        Map<String, String> uploadLicense;
        if(request.getLicense() != null)
        {
            uploadLicense = cloudinaryService.uploadImage(request.getLicense());
            organizer.setBusinessLicense(uploadLicense.get("url"));
            organizer.setBusinessLicenseId(uploadLicense.get("id"));
        }

        OrganizerResponseDTO response = organizerMapper.toOrganizerResponseDTO(organizerRepository.save(organizer));
        response.setCreatedDate(DateUtils.instantToString(organizer.getCreatedDate()));

        return response;
    }





}
