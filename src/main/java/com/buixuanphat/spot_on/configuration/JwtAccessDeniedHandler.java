package com.buixuanphat.spot_on.configuration;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Không được phép thực hiện hành động này");

        ObjectMapper mapper = new ObjectMapper();

        response.getWriter().write(mapper.writeValueAsString(apiResponse));

        response.flushBuffer();
    }
}