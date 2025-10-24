package com.take.take_breath.members;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.terms.MemberTerms;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("익명")
    private String nickname;

    private String profileImage;

    // 회원가입 시 이미지 디폴트값
    @PrePersist
    public void prePersist() {
        if(profileImage == null || profileImage.isEmpty()) {
            profileImage = "추가될 이미지 경로 예시로 넣어둘게요 /uploads/profile/default/default_profile.png";
        }
    }

    private String name;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;       // USER, COUNSELOR, ADMIN

    @Enumerated(EnumType.STRING)
    private Status status;     // PENDING, ACTIVE, SUSPENDED

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTerms> memberTermsList = new ArrayList<>();

    private boolean emailVerified = false;


    // 상담사 프로필 연결
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Counselor counselor;

}
