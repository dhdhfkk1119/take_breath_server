package com.take.take_breath.admin.view.dto;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorLicense;
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
    private final List<LicenseDTO> licenses;

    public CounselorListResponse(Counselor counselor) {
        this.id = counselor.getId();
        this.name = counselor.getMember() != null ? counselor.getMember().getName() : "N/A";
        this.email = counselor.getMember() != null ? counselor.getMember().getEmail() : "N/A";
        this.specialty = counselor.getSpecialty() != null ? counselor.getSpecialty() : "미정";

        // 자격증 리스트
        this.licenses = counselor.getLicenses() != null
                ? counselor.getLicenses().stream()
                .map(license -> new LicenseDTO(license))
                .collect(Collectors.toList())
                : List.of();
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