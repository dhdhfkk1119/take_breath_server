package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.MemberListResponse;
import com.take.take_breath.admin.view.dto.MemberStatsResponse;
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

    // 전체 회원 조회
    public List<MemberListResponse> getAllMembers() {
        return memberRepository.findAllActiveMembers()
                .stream()
                .map(m -> new MemberListResponse(m))
                .toList();
    }


    // 상태별 회원 조회
    public List<MemberListResponse> getMembersByStatus(Status status) {
        return memberRepository.findByStatus(status)
                .stream()
                .map(m -> new MemberListResponse(m))
                .toList();
    }

    // 탈퇴 대기 회원 조회
    public List<MemberListResponse> getWithdrawalMembers() {
        return memberRepository.findByStatus(Status.WITHDRAWAL)
                .stream()
                .map(m -> new MemberListResponse(m))
                .toList();
    }

    // 회원 통계
    public MemberStatsResponse getMemberStats() {
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(Status.ACTIVE);
        long suspendedMembers = memberRepository.countByStatus(Status.SUSPENDED);
        long withdrawalMembers = memberRepository.countByStatus(Status.WITHDRAWAL);

        // TODO: 도우미 버튼 클릭 수는 별도 테이블 필요
        long helpButtonClicks = 0;

        return new MemberStatsResponse(
                totalMembers,
                activeMembers,
                suspendedMembers,
                withdrawalMembers,
                helpButtonClicks
        );
    }
}