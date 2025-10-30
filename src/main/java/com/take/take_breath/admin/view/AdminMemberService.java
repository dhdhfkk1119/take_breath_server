package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.MemberStatsDTO;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMemberService {

    private final MemberRepository memberRepository;

    /**
     * 전체 회원 조회
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAllActiveMembers();
    }

    /**
     * 회원 통계 조회
     */
    public MemberStatsDTO getMemberStats() {
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(Status.ACTIVE);
        long suspendedMembers = memberRepository.countByStatus(Status.SUSPENDED);
        long withdrawalMembers = memberRepository.countByStatus(Status.WITHDRAWAL);

        // TODO: 도우미 버튼 클릭 수는 별도 테이블 필요
        long helpButtonClicks = 0;

        return new MemberStatsDTO(
                totalMembers,
                activeMembers,
                suspendedMembers,
                withdrawalMembers,
                helpButtonClicks
        );
    }

    /**
     * 탈퇴 대기 회원 목록 조회 (관리자 모니터링용)
     */
    public List<Member> getWithdrawalMembers() {
        return memberRepository.findByStatus(Status.WITHDRAWAL);
    }
}