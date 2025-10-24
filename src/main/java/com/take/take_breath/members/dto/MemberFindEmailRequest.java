package com.take.take_breath.members.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberFindEmailRequest {
    @NotEmpty(message = "이름은 필수 입니다")
    private String name;

    @NotEmpty(message = "전화번호는 필수 입니다")
    private String phone;

}
