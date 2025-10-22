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
    private String license;
    private String specialty;
    private String introduction;

    private boolean emailVerified = false;

}
