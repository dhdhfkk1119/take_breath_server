package com.take.take_breath.terms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // ex) 서비스 이용약관, 개인정보 처리방침

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean required; // 필수 여부
}
