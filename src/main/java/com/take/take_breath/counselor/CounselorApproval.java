package com.take.take_breath.counselor;

import com.take.take_breath.members.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "counselor_approval_tb")
public class CounselorApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor; // 어떤 상담사

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String reason; // 거절 사유
}
