package com.take.take_breath.members.dto;

import lombok.Data;

public class MemberRequestTo {

    @Data
    public static class MemberLoginRequest{
        private String email;
        private String password;
        private boolean autoLogin; // 자동 로그인 여부
    }


}
