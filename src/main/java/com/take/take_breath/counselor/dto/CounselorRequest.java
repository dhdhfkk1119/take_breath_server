package com.take.take_breath.counselor.dto;

import com.take.take_breath.counselor.entity.Counselor;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselorRequest {
    // 회원 정보
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;

    // 약관
    private boolean termsService;
    private boolean termsPrivacy;
    private boolean termsThirdParty;
    private boolean termsMarketing;

    // 상담사 정보
    private String license;
    private String specialty;
    private String introduction;
    private String gender;
    private String profileImage;
    private String hashtags;
    private int price;


    public Counselor toEntity(Member member) {
        return Counselor.builder()
                .member(member)
                .license(license)
                .specialty(specialty)
                .introduction(introduction)
                .gender(gender)
                .profileImage(profileImage)
                .hashtags(hashtags)
                .price(price)
                .build();
    }
}
