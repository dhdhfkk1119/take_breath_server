package com.take.take_breath.counselor.dto;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorLicense;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselorLicenseRequest {

    private String licenseName;
    private String licenseNumber;
    private String licenseRegiNumber;
    private String licenseImage;

    public CounselorLicense toEntity(Counselor counselor) {
        return CounselorLicense.builder()
                .licenseName(licenseName)
                .licenseNumber(licenseNumber)
                .licenseRegiNumber(licenseRegiNumber)
                .licenseImage(licenseImage)
                .counselor(counselor)
                .build();
    }
}