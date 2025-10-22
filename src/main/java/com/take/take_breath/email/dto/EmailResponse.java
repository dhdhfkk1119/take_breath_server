package com.take.take_breath.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailResponse {

    // 이메일 인증 확인
    private boolean emailVerified;
}
