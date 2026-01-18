package com.buixuanphat.spot_on.dto.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequestDTO {
    @Size(min = 3, max = 20, message = "Tên phải lớn hơn 3 kí tự và bé hơn 20 kí tự")
    String firstname;
    @Size(min = 3, max = 20, message = "Họ phải lớn hơn 3 kí tự và bé hơn 20 kí tự")
    String lastname;
    @Email(message = "Email không hợp lệ")
    String email;
    @Size(min = 8, max = 16, message = "Mật khẩu phải có độ dài từ 8 đến 16 kí tự")
    String password;
    MultipartFile avatar;
    Integer coins;
    String tier;
    String role;
    Boolean active;
}
