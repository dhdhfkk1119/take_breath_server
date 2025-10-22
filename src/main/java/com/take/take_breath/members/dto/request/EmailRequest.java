package com.take.take_breath.members.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotEmpty(message = "이메일은 필수 입니다")
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;

    private String code;
}
