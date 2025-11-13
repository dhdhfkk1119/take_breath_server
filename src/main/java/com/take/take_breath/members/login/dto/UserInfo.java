package com.take.take_breath.members.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String provider;  // naver, google, kakao
    private String socialId;
    private String email;
    private String name;
    private String profileImage;
}