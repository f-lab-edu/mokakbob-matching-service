package com.mokakbob.auth.config;

import com.mokakbob.auth.handler.OAuth2SuccessLoginHandler;
import com.mokakbob.auth.service.CustomOAuth2UserService;
import com.mokakbob.common.path.permit.PermitPath;
import com.mokakbob.auth.filter.ExceptionHandlingFilter;
import com.mokakbob.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String WILD_CARD_PATH = "/**";

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessLoginHandler successLoginHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlingFilter exceptionHandlingFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 기본 보안 설정 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // 세션을 STATELESS로 설정 (JWT 기반)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                PermitPath.AUTH_BASE + WILD_CARD_PATH,          // 일반 회원가입
                                PermitPath.EMAIL_BASE + WILD_CARD_PATH,        // 이메일 인증
                                PermitPath.ADMIN_BASE + WILD_CARD_PATH,        // admin
                                "/login/oauth2/**",                         // Oauth 콜백 URI
                                "/favicon.ico",
                                "/error",
                                "/actuator/**",              // 헬스 체크 경로 추가
                                "/ws-connect/**",             // 웹소켓 경로 추가
                                "/ws/**"                       // k6 test
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(successLoginHandler)  // 로그인 성공 시 처리
                )

                // 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlingFilter, JwtAuthenticationFilter.class)

                // 인증 실패 예외 핸들러
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}
