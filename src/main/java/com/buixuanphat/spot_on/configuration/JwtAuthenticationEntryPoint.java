package com.buixuanphat.spot_on.configuration;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.exception.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(ErrorMessage.UNAUTHENTICATED.getCode());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");


        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(ErrorMessage.UNAUTHENTICATED.getMessage());

        ObjectMapper mapper = new ObjectMapper();

        response.getWriter().write(mapper.writeValueAsString(apiResponse));

        response.flushBuffer();
    }
}
