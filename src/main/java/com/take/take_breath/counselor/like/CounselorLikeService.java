package com.take.take_breath.counselor.like;

import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CounselorLikeService {

    private final CounselorLikeRepository likeRepository;
    private final CounselorRepository counselorRepository;
    private final MemberRepository memberRepository;

    /**
     * 좋아요 토글 (누르면 추가, 다시 누르면 취소)
     */
    public boolean toggleLike(Long counselorId, Long memberId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return likeRepository.findByMemberAndCounselor(member, counselor)
                .map(like -> {
                    likeRepository.delete(like);
                    return false; // 좋아요 취소
                })
                .orElseGet(() -> {
                    likeRepository.save(CounselorLike.builder()
                            .member(member)
                            .counselor(counselor)
                            .build());
                    return true; // 좋아요 추가
                });
    }

    /**
     * 내가 좋아요 누른 상담사 목록 조회 (페이지 네이션)
     */
    public PageUtil.PageResponse<CounselorResponse> getLikedCounselors(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Page<CounselorLike> page = likeRepository.findAllByMember(member, pageable);

        List<CounselorResponse> content = page.getContent().stream()
                .map(like -> {
                    Counselor counselor = like.getCounselor();
                    CounselorResponse dto = CounselorResponse.from(counselor);
                    dto.setLikeCount(countLikes(counselor.getId()));
                    dto.setLikedByMe(true);
                    return dto;
                })
                .toList();

        return PageUtil.PageResponse.of(page, content);
    }


    /**
     * 특정 상담사의 좋아요 개수 조회
     */
    public long countLikes(Long counselorId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));
        return likeRepository.countByCounselor(counselor);
    }
    /**
     * 현재 회원이 특정 상담사를 좋아요 눌렀는지 여부
     */
    public boolean isLikedByMember(Long counselorId, Long memberId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return likeRepository.findByMemberAndCounselor(member, counselor).isPresent();
    }
}
