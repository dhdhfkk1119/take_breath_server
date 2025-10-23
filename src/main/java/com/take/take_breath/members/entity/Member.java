package com.take.take_breath.members.entity;

import com.take.take_breath.counselor.entity.Counselor;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import jakarta.persistence.*;
import lombok.*;

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


    private String name;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;       // USER, COUNSELOR, ADMIN

    @Enumerated(EnumType.STRING)
    private Status status;     // PENDING, ACTIVE, SUSPENDED

    private boolean emailVerified = false;

    // 회원 약관
    private boolean termsService; // 서비스 이용약관 동의 (필수)
    private boolean termsPrivacy; // 개인정보 수집 동의 (필수)
    private boolean termsThirdParty; // 제 3자 제공 동의 (필수)
    private boolean termsMarketing; // 마케팅 수신 동의 (선택)

    // 상담사 프로필 연결
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Counselor counselor;

}
