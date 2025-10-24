package com.take.take_breath.members.dto;

import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.entity.Member;
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

    // 비밀번호 확인
    private String confirmPassword;

    // signup시 사용
    private String name;
    private String phone;
    private String address;

    // 약간 동의
    private boolean termsService;
    private boolean termsPrivacy;
    private boolean termsThirdParty;
    private boolean termsMarketing;

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
                .termsService(req.isTermsService())
                .termsPrivacy(req.isTermsPrivacy())
                .termsThirdParty(req.isTermsThirdParty())
                .termsMarketing(req.isTermsMarketing())
                .build();
    }


}
