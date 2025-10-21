package com.take.take_breath.members.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyCodeRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;
}
