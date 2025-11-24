package com.buixuanphat.spot_on.dto.organizer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrganizerCreateRequestDTO {

    @Size(min = 3, max = 100, message = "NAME_INVALID")
    String name;
    @Size(min = 10, max = 13, message = "TAX_CODE_INVALID")
    @Pattern(regexp = "\\d+", message = "TAX_CODE_INVALID")
    String taxCode;
    @Pattern(regexp = "\\d+", message = "BANK_ACCOUNT_INVALID")
    String bankNumber;
    String bank;
    @Email(message = "EMAIL_INVALID")
    String email;
    @Pattern(regexp = "^0\\d{9}$", message = "PHONE_NUMBER_INVALID")
    String phoneNumber;
    @Size(min = 3, max = 100, message = "ADDRESS_INVALID")
    String address;
    @Size(min = 3,max = 100, message = "DESCRIPTION_INVALID")
    String description;

}
