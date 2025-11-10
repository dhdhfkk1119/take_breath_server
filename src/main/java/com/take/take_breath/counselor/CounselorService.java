package com.take.take_breath.counselor;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.counselor.like.CounselorLikeService;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CounselorService {

    private final CounselorLikeService counselorLikeService;
    private final CounselorRepository counselorRepository;
    private final MemberRepository memberRepository;
    private final CounselorApprovalRepository counselorApprovalRepository;
    private final MemberService memberService;

    @Transactional
    public CounselorResponse signup(CounselorRequest req) {
        Member member = memberService.signup(req.toMemberRequest());
        Counselor counselor = req.toEntity(member);

        counselorRepository.saveAndFlush(counselor);
        CounselorApproval approval = req.toApproval(counselor);
        counselorApprovalRepository.save(approval);

        return CounselorResponse.from(counselor);
    }

    /**
     * ✅ 상담사 전체 조회 (JWT 기반)
     */
    public List<CounselorResponse> getAllCounselors(HttpServletRequest request) {
        String memberEmail = (String) request.getAttribute("memberEmail");

        return counselorRepository.findAll().stream()
                .map(c -> {
                    CounselorResponse dto = CounselorResponse.from(c);
                    dto.setLikeCount(counselorLikeService.countLikes(c.getId()));

                    if (memberEmail != null) {
                        Member member = memberRepository.findByEmail(memberEmail)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
                        dto.setLikedByMe(counselorLikeService.isLikedByMember(c.getId(), memberEmail));
                    } else {
                        dto.setLikedByMe(false);
                    }
                    return dto;
                })
                .toList();
    }

    /**
     * ✅ 페이지네이션 (좋아요는 포함 안 함 — 필요시 추가 가능)
     */
    public PageUtil.PageResponse<CounselorResponse> findAll(Pageable pageable) {
        Page<Counselor> counselorPage = counselorRepository.findAll(pageable);

        List<CounselorResponse> content = counselorPage.getContent().stream()
                .map(CounselorResponse::from)
                .collect(Collectors.toList());

        return PageUtil.PageResponse.of(counselorPage, content);
    }

    /**
     * ✅ 상담사 상세 조회 (좋아요 포함)
     */
    public CounselorResponse findById(Long counselorId, HttpServletRequest request) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        CounselorResponse dto = CounselorResponse.from(counselor);
        dto.setLikeCount(counselorLikeService.countLikes(counselorId));

        String memberEmail = (String) request.getAttribute("memberEmail");
        if (memberEmail != null) {
            Member member = memberRepository.findByEmail(memberEmail)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
            dto.setLikedByMe(counselorLikeService.isLikedByMember(counselorId, memberEmail));
        } else {
            dto.setLikedByMe(false);
        }

        return dto;
    }

    /**
     * ✅ 해시태그 검색
     */
    public List<CounselorResponse> searchByHashtags(String keyword) {
        return counselorRepository.findByHashtagsContaining(keyword).stream()
                .map(CounselorResponse::from)
                .collect(Collectors.toList());
    }
}
