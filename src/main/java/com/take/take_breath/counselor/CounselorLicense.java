package com.take.take_breath.counselor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "counselor_license_tb")
public class CounselorLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licenseName;   // 자격증 이름
    private String licenseNumber;  // 전문 분야
    private String licenseRegiNumber; // 상세 설명
    private String licenseImage;  // 자격증 이미지 경로

    // Counselor와 N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id")
    private Counselor counselor;
}