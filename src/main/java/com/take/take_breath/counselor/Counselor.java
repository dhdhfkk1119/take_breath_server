package com.take.take_breath.counselor;

import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "counselor_tb")
public class Counselor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 1명당 상담사 1명 (1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String introduction; // 상담사 소개
    private String gender; // 성별
    private String profileImage; // 프로필 이미지 경로
    private String specialty; // 전문 분야 (ex: 우울, 불안, 대인관계 등)
    private int price; // 상담료
    private String hashtags; // 해시태그 (추후 분리 가능)

    // 자격증 리스트 (1:N 관계)
    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CounselorLicense> licenses;
}
