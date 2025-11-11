package com.take.take_breath.counselor.dto;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorApproval;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.dto.MemberRequest;
import com.take.take_breath.members.Member;
import com.take.take_breath.terms.dto.MemberTermsRequest;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselorRequest {
    // 회원 정보
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String phone;
    private String address;

    // 약관
    private List<MemberTermsRequest> agreements;

    // 상담사 정보
    private String specialty;
    private String introduction;
    private String gender;
    private String profileImage;
    private List<String> hashtags;
    private int price;

    // 자격증 목록
    private List<CounselorLicenseRequest> licenses;


    public Counselor toEntity(Member member) {
        Counselor counselor = Counselor.builder()
                .member(member)
                .specialty(specialty)
                .introduction(introduction)
                .gender(gender)
                .profileImage(profileImage)
                .hashtags(hashtags != null && !hashtags.isEmpty()
                        ? String.join("#", hashtags)
                        : null)
                .price(price)
                .status(Status.PENDING)
                .build();

        // 라이선스 목록 변환
        if (licenses != null && !licenses.isEmpty()) {
            counselor.setLicenses(
                    licenses.stream()
                            .map(licenseReq -> licenseReq.toEntity(counselor))
                            .collect(Collectors.toList())
            );
        }

        return counselor;
    }

    // 승인 엔티티
    public CounselorApproval toApproval(Counselor counselor) {
        return CounselorApproval.builder()
                .counselor(counselor)
                .status(Status.PENDING)
                .reason(null)
                .build();
    }

    public MemberRequest toMemberRequest() {
        return MemberRequest.builder()
                .email(this.email)
                .password(this.password)
                .confirmPassword(this.password)
                .name(this.name)
                .phone(this.phone)
                .address(this.address)
                .agreements(this.agreements)
                .role(Role.COUNSELOR)
                .build();
    }
}
