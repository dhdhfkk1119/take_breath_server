package com.take.take_breath.members.login.newlogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberResponseTo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Login{
        private String accessToken;
        private String refreshToken;
        private Long id;
        private String nickName;
        private String email;
        private String profileImageUrl;
        private String role;
        private String status;
    }

    @Data
    public static  class isCheckEmailDTO{
        private String message;
        private boolean isCheck;
    }
}
