package com.take.take_breath.members;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDTO {

    @Getter
    @NoArgsConstructor
    public static class SignupRequest {
        private String email;
        private String password;
        private String name;
        private String phone;
        private String address;
    }

    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class TokenResponse {
        private String token;
    }

    // 이메일 코드 발송
    @Getter
    @NoArgsConstructor
    public static class SendCodeRequest {
        @NotBlank
        @Email
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class VerifyCodeRequest {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String code;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerificationResponse {
        private boolean emailVerified;
    }
}
