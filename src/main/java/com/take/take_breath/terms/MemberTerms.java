package com.take.take_breath.terms;

import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_terms_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id")
    private Terms terms;

    private boolean agreed;
    private LocalDateTime agreedAt; // 동의 시각

}
