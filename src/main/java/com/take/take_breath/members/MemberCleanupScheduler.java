package com.take.take_breath.members;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberCleanupScheduler {

    private final MemberRepository memberRepository;
    private final MemberWithdrawalService memberWithdrawalService;

    /**
     * 매일 새벽 3시에 3개월 지난 탈퇴 대기 회원 자동 삭제
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredWithdrawalMembers() {
        log.info("=== 회원 자동 관리 스케줄러 시작 ===");

        List<Member> withdrawalMembers = memberRepository.findByStatus(Status.WITHDRAWAL);
        int deletedCount = 0;

        for (Member member : withdrawalMembers) {
            if (memberWithdrawalService.isWithdrawalPeriodExpired(member)) {
                log.info("회원 자동 삭제: email={}", member.getEmail());
                memberRepository.delete(member);
                deletedCount++;
            }
        }

        log.info("=== 탈퇴 대기 회원 자동 삭제 완료: {}명 ===", deletedCount);
    }

    /**
     * 매일 새벽 3시 - 정지 기간이 끝난 회원 자동 해제
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void unlockSuspendedMembers() {
        log.info("=== 정지 해제 스케줄러 시작 ===");

        List<Member> suspendedMembers = memberRepository.findByStatus(Status.SUSPENDED);
        int unlockedCount = 0;

        for (Member member : suspendedMembers) {
            if (member.getSuspendedUntil() != null &&
                    member.getSuspendedUntil().isBefore(LocalDateTime.now())) {

                member.setStatus(Status.ACTIVE);
                member.setSuspendedUntil(null);
                memberRepository.save(member);
                unlockedCount++;

                log.info("회원 정지 해제됨: email={}", member.getEmail());
            }
        }

        log.info("=== 정지 해제 스케줄러 완료: {}명 ===", unlockedCount);
    }
}
