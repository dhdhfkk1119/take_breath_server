package com.take.take_breath.members;

public enum Status {
    PENDING, // 관리자 승인 대기
    ACTIVE,
    SUSPENDED, // 이용 정지
    REJECTED, // 승인 거절
    WITHDRAWAL // 탈퇴 대기
}
