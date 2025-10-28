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
    }
}
