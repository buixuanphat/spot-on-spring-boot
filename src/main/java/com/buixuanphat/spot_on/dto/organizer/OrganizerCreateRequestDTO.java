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

    @Size(min = 3, max = 100, message = "Tên Ban tổ chức phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    String name;
    @Size(min = 10, max = 13, message = "Mã số thuế không hợp lệ")
    @Pattern(regexp = "\\d+", message = "Mã số thuế không hợp lệ")
    String taxCode;
    @Pattern(regexp = "\\d+", message = "Số tài khoản không hợp lệ")
    String bankNumber;
    String bank;
    @Email(message = "Email không hợp lệ")
    String email;
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    String phoneNumber;
    @Size(min = 3, max = 100, message = "Địa chỉ phải lớn hơn 3 kí tự và bé hơn 100 kí tự")
    String address;
    @Size(min = 3,max = 500, message = "Mô tả phải lớn hơn 3 kí tự và bé hơn 500 kí tự")
    String description;
    MultipartFile avatar;
    MultipartFile license;
    String status;

}
