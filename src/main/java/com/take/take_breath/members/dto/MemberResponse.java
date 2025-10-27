package com.take.take_breath.members.dto;

import com.take.take_breath.members.Gender;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private String token;
    private String email;
    private String nickname;
    private String profileImage;
    private String name;
    private String phone;
    private String address;
    private Role role;
    private Status status;
    private Gender gender;

    public MemberResponse(Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.role = member.getRole();
        this.status = member.getStatus();
        this.gender = member.getGender();
    }
}
