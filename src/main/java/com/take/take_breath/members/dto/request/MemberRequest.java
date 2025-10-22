package com.take.take_breath.members.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest {

    @NotEmpty(message = "이메일은 필수 입니다")
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입니다")
    private String password;

    // signup시 사용
    private String name;
    private String phone;
    private String address;

}
