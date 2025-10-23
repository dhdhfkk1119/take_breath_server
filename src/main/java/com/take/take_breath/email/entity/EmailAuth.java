package com.take.take_breath.email.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_auth_tb")
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    // 이메일 인증 여부
    @Column(nullable = false)
    private boolean verified;

    // 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }
}