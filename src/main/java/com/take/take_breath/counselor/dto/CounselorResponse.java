package com.take.take_breath.counselor.dto;

import com.take.take_breath.counselor.Counselor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselorResponse {
    private Long id;
    private String name;
    private String license;
    private String specialty;
    private String introduction;
    private String profileImage;
    private String hashtags;
    private int price;

    public static CounselorResponse from(Counselor counselor) {
        return CounselorResponse.builder()
                .id(counselor.getId())
                .name(counselor.getMember().getName())
                .license(counselor.getLicense())
                .specialty(counselor.getSpecialty())
                .introduction(counselor.getIntroduction())
                .profileImage(counselor.getProfileImage())
                .hashtags(counselor.getHashtags())
                .price(counselor.getPrice())
                .build();
    }
}
