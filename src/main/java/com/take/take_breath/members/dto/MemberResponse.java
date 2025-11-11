package com.take.take_breath.members.dto;

import com.take.take_breath.members.Gender;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String accessToken;
    private String email;
    private String nickName;
    private String profileImage;
    private String name;
    private String phone;
    private String address;
    private Role role;
    private Status status;
    private Gender gender;

    // 탈퇴 관련
    private Boolean needWithdrawalConfirm;  // 탈퇴 확인 필요 여부
    private Long daysLeft;                   // 남은 일수

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickName = member.getNickName();
        this.profileImage = member.getProfileImage();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.role = member.getRole();
        this.status = member.getStatus();
        this.gender = member.getGender();
    }
}
