package com.buixuanphat.spot_on.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ Builder
public class CreateUserRequestDTO {
    @Email(message = "Email không hợp lệ")
    String email;
    @Size(min = 8, max = 16, message = "Mật khẩu phải dài từ 8 đến 16 kí tự")
    String password;
    MultipartFile avatar;
    @Size(min = 3, max = 20, message = "Tên phải dài từ 3 đến 20 kí tự")
    String firstname;
    @Size(min = 3, max = 20, message = "Họ khẩu phải dài từ 3 đến 20 kí tự")
    String lastname;
}
