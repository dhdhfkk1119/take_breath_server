package com.take.take_breath._core._config;

import com.take.take_breath._core._jwt.JwtInterceptor;
import com.take.take_breath._core.auth.AuthInterceptor;
import com.take.take_breath.admin.AdminAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AuthInterceptor authInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*", "null")  // 모든 origin 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")  // JWT 헤더 노출
                .allowCredentials(true)
                .maxAge(3600);  // preflight 캐싱
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/members/**",
                        "/api/members/signup",           // 회원가입
                        "/api/members/login",            // 로그인
                        "/api/members/find-email",       // 이메일 찾기
                        "/api/members/password/reset-request", // 비밀번호 인증 코드 전송
                        "/api/members/password/reset",    // 비밀번호 재설정
                        "/api/emails/**",                // 이메일 인증 관련
                        "/api/counselors/signup",
                        "/api/counselors/login",
                        "/api/admin/**",
                        "/error",                        // 스프링 기본 에러
                        "/api/test/**",                   // 테스트용
                        "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/uploads/**",
                        "/api/terms/**"
                );

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");

        // 관리자 세션 인터셉터
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/view/**")
                .excludePathPatterns("/api/admin/view/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일을 웹에서 접근 가능하게 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
        // JS 파일 매핑
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // CSS 파일 매핑
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
    }

}