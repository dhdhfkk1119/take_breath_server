package com.take.take_breath.members;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception404;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberWithdrawalService {

    private final MemberRepository memberRepository;

    // 회원 탈퇴 요청
    @Transactional
    public void requestWithdrawal(String email, String reason) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        // 탈퇴 대기중인 경우
        if (member.getStatus() == Status.WITHDRAWAL) {
            throw new Exception400("이미 탈퇴 대기중입니다.");
        }

        member.setStatus(Status.WITHDRAWAL);
        member.setWithdrawalReason(reason);
        member.setWithdrawalRequestedAt(LocalDateTime.now());

        memberRepository.save(member);
        log.info("회원 탈퇴 요청 : memberId={}, reason={}, email={}", member.getId(), member.getWithdrawalReason(), member.getEmail());
    }

    // 탈퇴 취소 및 복구
    @Transactional
    public void cancelWithdrawal(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        if (member.getStatus() != Status.WITHDRAWAL) {
            throw new Exception400("탈퇴 대기 상태가 아닙니다.");
        }

        if (isWithdrawalPeriodExpired(member)) {
            throw new Exception400("복구 기간이 만료되었습니다.");
        }

        // 즉시 복구
        member.setStatus(Status.ACTIVE);
        member.setWithdrawalRequestedAt(null);
        member.setWithdrawalReason(null);

        memberRepository.save(member);

        log.info("회원 계정 복구: memberId={}, email={}", member.getId(), member.getEmail());
    }

    // 3개월 지났는지 체크
    public boolean isWithdrawalPeriodExpired(Member member) {
        if (member.getWithdrawalRequestedAt() == null) {
            return false;
        }

        LocalDateTime expiryDate = member.getWithdrawalRequestedAt().plusMonths(3);
        return LocalDateTime.now().isAfter(expiryDate);
    }

    // 삭제까지 남은 기간
    public long getDaysUntilDeletion(Member member) {
        if (member.getWithdrawalRequestedAt() == null) {
            return 0;
        }

        LocalDateTime expiryDate = member.getWithdrawalRequestedAt().plusMonths(3);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(expiryDate)) {
            return 0;
        }

        return ChronoUnit.DAYS.between(now, expiryDate);
    }

}
