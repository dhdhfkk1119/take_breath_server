package com.take.take_breath.members.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class MemberResponseTo {

    @Data
    @AllArgsConstructor
    @Builder
    public static class Login{
        private String accessToken;
        private String refreshToken; // 자동 로그인 시 발급, 아니면 null
        private Long id;
        private String name;
        private String nickName;
        private String email;
        private String profileImageUrl;
        private String role;
        private String status; // 현재 계정 상태 (ex: ACTIVE, WITHDRAWAL)
        private String phone;

        // (선택 사항) 탈퇴 대기 중인 경우에만 사용.
        // 이 필드가 null이 아니면 daysLeft만 반환.
        private Long daysLeft;

        // 1. 일반 로그인 응답을 위한 생성자 (탈퇴 상태가 아닐 때)
        public Login(String accessToken, String refreshToken, Long id, String name, String nickName, String email, String profileImageUrl, String role, String status, String phone) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.id = id;
            this.name = name;
            this.nickName = nickName;
            this.email = email;
            this.profileImageUrl = profileImageUrl;
            this.role = role;
            this.status = status;
            this.phone = phone;
            this.daysLeft = null; // 일반 로그인 시에는 null
        }

        // 2. 탈퇴 처리 중인 계정 응답을 위한 생성자 (Status.WITHDRAWAL 일 때)
        // 이 경우 accessToken만 발급하고 다른 정보는 최소화
        public Login(String accessToken, String status, Long daysLeft) {
            this.accessToken = accessToken;
            this.refreshToken = null;
            this.id = null;
            this.nickName = null;
            this.email = null;
            this.profileImageUrl = null;
            this.role = null;
            this.status = status; // "WITHDRAWAL"
            this.phone = null;
            this.daysLeft = daysLeft;
        }
    }

    @Data
    public static  class isCheckEmailDTO{
        private String message;
        private boolean isCheck;
    }
}
