package com.mokakbob.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.ApiErrorCode;
import com.mokakbob.common.exception.ApiException;
import com.mokakbob.common.exception.handler.response.CustomErrorResponse;
import com.mokakbob.global.exception.GlobalErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApiException e) {
            ApiErrorCode errorCode = e.getErrorCode();
            setErrorResponse(response,
                    errorCode.getHttpStatus(),
                    errorCode.getMessage(),
                    errorCode.getCustomCode());
        } catch (Exception e) {
            setErrorResponse(response,
                    GlobalErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
                    GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    GlobalErrorCode.INTERNAL_SERVER_ERROR.getCustomCode());
        }
    }

    private void setErrorResponse(HttpServletResponse response, int status, String message, String customCode) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CustomErrorResponse errorResponse = new CustomErrorResponse(customCode, message);

        response.getWriter()
                .write(objectMapper.writeValueAsString(errorResponse));
    }
}
