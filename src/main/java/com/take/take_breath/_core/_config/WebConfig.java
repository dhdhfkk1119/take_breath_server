package com.take.take_breath._core._config;

import com.take.take_breath._core._jwt.JwtInterceptor;
import com.take.take_breath.admin.AdminAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 인터셉터
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/members/signup",
                        "/api/members/login",
                        "/api/members/find-email",
                        "/api/members/password/reset-request",
                        "/api/members/password/reset",
                        "/api/counselors/signup",
                        "/api/counselors/login",
                        "/api/emails/**",
                        "/api/admin/**",              // 관리자 전체 제외 (이것만 있으면 됨)
                        "/api/test/**",
                        "/error",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico"
                );

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
    }
}