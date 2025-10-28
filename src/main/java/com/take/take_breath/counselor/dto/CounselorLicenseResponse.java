package com.take.take_breath.counselor.dto;

import com.take.take_breath.counselor.CounselorLicense;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselorLicenseResponse {
    private String licenseName;
    private String licenseNumber;
    private String licenseRegistrationNumber;
    private String licenseImage;

    public static CounselorLicenseResponse from(CounselorLicense license) {
        return CounselorLicenseResponse.builder()
                .licenseName(license.getLicenseName())
                .licenseNumber(license.getLicenseNumber())
                .licenseRegistrationNumber(license.getLicenseRegiNumber())
                .licenseImage(license.getLicenseImage())
                .build();
    }
}