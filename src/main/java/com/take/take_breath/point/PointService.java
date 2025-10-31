package com.take.take_breath.point;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {
    
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    
    /**
     * 내 포인트 잔액 조회
     */
    public PointResponse.BalanceDTO getBalance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));
        
        return PointResponse.BalanceDTO.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .point(member.getPoint())
                .build();
    }
    
    /**
     * 내 포인트 히스토리 조회
     */
    public Page<PointResponse.HistoryDTO> getHistory(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));
        
        Page<PointHistory> histories = pointHistoryRepository
                .findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        return histories.map(history -> new PointResponse.HistoryDTO(history));
    }
}