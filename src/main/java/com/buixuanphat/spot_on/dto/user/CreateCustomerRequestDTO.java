package com.buixuanphat.spot_on.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerRequestDTO {
    @Size(min = 3, max = 20, message = "FIRSTNAME_INVALID")
    String firstname;
    @Size(min = 3, max = 20, message = "LASTNAME_INVALID")
    String lastname;
    @Email(message = "EMAIL_INVALID")
    String email;
    @Size(min = 8, max = 16, message = "PASSWORD_INVALID")
    String password;
    String dateOfBirth;
}
