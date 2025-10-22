package com.take.take_breath.members.entity;

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

    // 상담사용
    private String license; // 자격
    private String specialty; // 상담분야(전문분야)
    private String introduction; // 소개
    private String gender; // 성별
    private String profileImage; // 프필 이미지

    private boolean emailVerified = false;

}
