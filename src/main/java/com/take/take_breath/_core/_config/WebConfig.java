package com.take.take_breath._core._config;

import com.take.take_breath._core._jwt.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/members/signup",           // 회원가입
                        "/api/members/login",            // 로그인
                        "/api/emails/**",                // 이메일 인증 관련
                        "/api/counselors/signup",
                        "/api/counselors/login",
                        "/h2-console/**",                // H2 콘솔
                        "/error",                        // 스프링 기본 에러
                        "/api/test/**"                   // 테스트용
                );
    }
}
