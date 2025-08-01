package com.mokakbob.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.common.util.TokenExtractor;
import com.mokakbob.global.filter.ExceptionHandlingFilter;
import com.mokakbob.global.filter.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private static final int FIRST_ORDER = 1;
    private static final int SECOND_ORDER = 2;

    @Bean
    public FilterRegistrationBean<ExceptionHandlingFilter> exceptionFilter(ObjectMapper mapper) {
        FilterRegistrationBean<ExceptionHandlingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ExceptionHandlingFilter(mapper));
        registration.setOrder(FIRST_ORDER);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(TokenExtractor extractor, TokenProvider provider) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(extractor, provider));
        registration.setOrder(SECOND_ORDER);
        return registration;
    }
}
