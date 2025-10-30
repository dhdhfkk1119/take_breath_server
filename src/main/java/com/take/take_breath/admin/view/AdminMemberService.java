package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.MemberStatsDTO;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public MemberStatsDTO getMemberStats() {
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(Status.ACTIVE);
        long suspendedMembers = memberRepository.countByStatus(Status.SUSPENDED);
        long deletedMembers = 0; // DB에서 삭제되므로 추적 불가 (나중에 soft delete 구현 시)

        // TODO: 도우미 버튼 클릭 수는 별도 테이블 필요
        long helpButtonClicks = 0;

        return new MemberStatsDTO(
                totalMembers,
                activeMembers,
                suspendedMembers,
                deletedMembers,
                helpButtonClicks
        );
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}