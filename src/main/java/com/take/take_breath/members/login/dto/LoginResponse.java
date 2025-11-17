package com.take.take_breath.members.login.dto;

import com.take.take_breath.members.dto.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private MemberResponse user;
}