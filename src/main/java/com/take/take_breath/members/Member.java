package com.take.take_breath.members;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.terms.MemberTerms;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_tb")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ColumnDefault("'익명'")
    private String nickName;

    private String profileImage;

    // 회원가입 시 이미지 디폴트값
    @PrePersist
    public void prePersist() {
        if(profileImage == null || profileImage.isEmpty()) {
            profileImage = "http://localhost:8080/uploads/member-images/default_profile.png";
        }

        if (nickName == null || nickName.isBlank()) {
            nickName = "익명";
        }
    }

    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 사용 시 기본값 설정
    private LoginType loginType = LoginType.LOCAL;

    private String name;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;       // USER, COUNSELOR, ADMIN

    @Enumerated(EnumType.STRING)
    private Status status;     // PENDING, ACTIVE, SUSPENDED

    @Enumerated(EnumType.STRING)
    private Gender gender;      // MALE, FEMALE

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTerms> memberTermsList = new ArrayList<>();

    private boolean emailVerified = false;

    @Column(length = 512)
    private String refreshToken;

    @Builder.Default
    @Column(nullable = false)
    private Long point = 0L;

    @Column(name = "withdrawal_requested_at")
    private LocalDateTime withdrawalRequestedAt;

    @Column(name = "withdrawal_reason", length = 500)
    private String withdrawalReason;

    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil;

    // 생성일 추가 (회원 가입일)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    private String provider; // 소셜로그인 제공자
    private String socialId; //

    @Column(name = "fcm_token", length = 512)
    private String fcmToken; // firebase FCM 토큰 -> flutter 에서 받은 토큰을 백엔드로 보냄


    // 상담사 프로필 연결
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Counselor counselor;

}
