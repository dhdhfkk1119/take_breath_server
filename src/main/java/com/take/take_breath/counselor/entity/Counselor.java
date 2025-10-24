package com.take.take_breath.counselor.entity;

import com.take.take_breath.members.entity.Member;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String license; // 자격
    private String specialty; // 전문분야
    private String introduction; // 소개
    private String gender; // 성별
    private String profileImage; // 프로필 이미지
    private String hashtags; // 해시태그
    private int price; // 상담료
}
