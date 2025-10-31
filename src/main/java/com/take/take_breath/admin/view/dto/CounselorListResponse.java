package com.take.take_breath.admin.view.dto;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorLicense;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorLicense;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.Status;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class CounselorListResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String specialty;
    private final int price;
    private final int point;
    private final Status status;
    private final String introduction;
    private final String hashtags;
    private final Member member;
    private final List<LicenseDTO> licenses;

    // 상태 체크 (Mustache용)
    private final boolean isActive;
    private final boolean isPending;
    private final boolean isSuspended;
    private final boolean isRejected;

    public CounselorListResponse(Counselor counselor) {
        this.id = counselor.getId();
        this.name = counselor.getMember() != null ? counselor.getMember().getName() : "N/A";
        this.email = counselor.getMember() != null ? counselor.getMember().getEmail() : "N/A";
        this.specialty = counselor.getSpecialty() != null ? counselor.getSpecialty() : "미정";
        this.price = counselor.getPrice();
        this.point = counselor.getPoint();
        this.status = counselor.getStatus();
        this.introduction = counselor.getIntroduction();
        this.hashtags = counselor.getHashtags();
        this.member = counselor.getMember();

        // 자격증 리스트
        this.licenses = counselor.getLicenses() != null
                ? counselor.getLicenses().stream()
                .map(l -> new LicenseDTO(l))
                .collect(Collectors.toList())
                : List.of();

        // 상태 체크
        this.isActive = counselor.getStatus() == Status.ACTIVE;
        this.isPending = counselor.getStatus() == Status.PENDING;
        this.isSuspended = counselor.getStatus() == Status.SUSPENDED;
        this.isRejected = counselor.getStatus() == Status.REJECTED;
    }

    @Getter
    public static class LicenseDTO {
        private final String licenseName;
        private final String licenseNumber;
        private final String licenseRegiNumber;
        private final String licenseImage;

        public LicenseDTO(CounselorLicense license) {
            this.licenseName = license.getLicenseName();
            this.licenseNumber = license.getLicenseNumber();
            this.licenseRegiNumber = license.getLicenseRegiNumber();
            this.licenseImage = license.getLicenseImage();
        }
    }
}