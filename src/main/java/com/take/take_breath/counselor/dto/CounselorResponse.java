package com.take.take_breath.counselor.dto;

import com.take.take_breath.counselor.Counselor;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> hashtags;
    private String gender;
    private int price;
    private long likeCount;
    private boolean likedByMe;

    private List<CounselorLicenseResponse> licenses; // 자격증 목록

    public static CounselorResponse from(Counselor counselor) {
        return CounselorResponse.builder()
                .id(counselor.getId())
                .name(counselor.getMember().getName())
                .specialty(counselor.getSpecialty())
                .introduction(counselor.getIntroduction())
                .gender(counselor.getGender())
                .profileImage(counselor.getProfileImage())
                .hashtags(convertHashtags(counselor.getHashtags()))
                .price(counselor.getPrice())
                .licenses(
                        counselor.getLicenses() != null
                                ? counselor.getLicenses().stream()
                                .map(license -> CounselorLicenseResponse.from(license))
                                .collect(Collectors.toList())
                                : null
                )
                .likeCount(0)
                .likedByMe(false)
                .build();
    }

    private static List<String> convertHashtags(String hashtags) {
        if (hashtags == null || hashtags.isBlank()) return List.of();
        return Arrays.stream(hashtags.split("#"))
                .filter(tag -> !tag.isBlank())
                .map(tag -> "#" + tag.trim())
                .collect(Collectors.toList());
    }
}