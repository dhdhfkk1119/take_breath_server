package com.take.take_breath.members.dto;

import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.Member;
import com.take.take_breath.terms.dto.MemberTermsRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

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

    // 비밀번호 확인
    private String confirmPassword;

    // signup시 사용
    private String name;
    private String phone;
    private String address;

    private Role role;

    public Member toEntity(MemberRequest req, String encodedPassword, Status status){
        return Member.builder()
                .email(req.getEmail())
                .password(encodedPassword)
                .name(req.getName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .role(role)
                .status(status)
                .emailVerified(true)
                .build();
    }

    private List<MemberTermsRequest> agreements;


}
