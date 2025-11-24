package com.buixuanphat.spot_on.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ApiResponse <T>{
    boolean success;
    String message;
    T data;

}
