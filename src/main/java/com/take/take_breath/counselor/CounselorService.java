package com.take.take_breath.counselor;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core._utils.UploadFile;
import com.take.take_breath.counselor.dto.CounselorProfileUpdateRequest;
import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.counselor.like.CounselorLikeRepository;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final UploadFile uploadFile;
    private final CounselorLikeRepository counselorLikeRepository;

    @Transactional
    public CounselorResponse signup(CounselorRequest req) {
        Member member = memberService.signup(req.toMemberRequest());
        Counselor counselor = req.toEntity(member);

        counselorRepository.saveAndFlush(counselor);
        CounselorApproval approval = req.toApproval(counselor);
        counselorApprovalRepository.save(approval);

        return CounselorResponse.from(counselor,false);
    }

    /**
     * 상담사 전체 조회 (JWT 기반)
     */
    public List<CounselorResponse> getAllCounselors(HttpServletRequest request) {
        String memberEmail = (String) request.getAttribute("memberEmail");

        return counselorRepository.findAll().stream()
                .map(c -> {
                    CounselorResponse dto = CounselorResponse.from(c,false);
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
     * 페이지네이션
     */
    public PageUtil.PageResponse<CounselorResponse> findAll(Pageable pageable, Long memberId) {

        Page<Counselor> counselorPage = counselorRepository.findAll(pageable);


        List<CounselorResponse> content = counselorPage.getContent().stream()
                .map(counselor -> {

                    boolean likedByMe = false;
                    if (memberId != null) {

                        likedByMe = counselorLikeRepository.existsByCounselorIdAndMemberId(
                                counselor.getId(),
                                memberId
                        );
                    }

                    // 좋아요 상태를 포함하여 DTO를 생성합니다.
                    return CounselorResponse.from(counselor, likedByMe);
                })
                .collect(Collectors.toList());

        return PageUtil.PageResponse.of(counselorPage, content);
    }

    /**
     * 상담사 상세 조회 (좋아요 포함)
     */
    public CounselorResponse findById(Long counselorId, HttpServletRequest request) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        boolean likedByMe = false;
        String memberEmail = (String) request.getAttribute("memberEmail");

        if (memberEmail != null) {
            likedByMe = counselorLikeService.isLikedByMember(counselorId, memberEmail);
        }

        CounselorResponse dto = CounselorResponse.from(counselor, likedByMe);
        dto.setLikeCount(counselorLikeService.countLikes(counselorId));

        return dto;
    }

    /**
     * 해시태그 검색
     */
    public List<CounselorResponse> searchByHashtags(String keyword) {
        return counselorRepository.findByHashtagsContaining(keyword).stream()
                .map(c -> CounselorResponse.from(c, false))
                .collect(Collectors.toList());
    }

    // 상담사 프로필 수정
    @Transactional
    public void updateProfile(Long id, CounselorProfileUpdateRequest req) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 상담사를 찾을 수 없습니다."));

        // 자기소개 수정
        if (req.getIntroduction() != null && !req.getIntroduction().isBlank()) {
            counselor.setIntroduction(req.getIntroduction());
        }

        // 프로필 이미지 수정
        MultipartFile profileImage = req.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 기존 이미지 삭제
                uploadFile.deleteProfileImage(counselor.getProfileImage(), "counselor");

                // 업로드 (저장 경로는 counselor-images/)
                String savedPath = uploadFile.uploadImage(profileImage, "counselor");
                counselor.setProfileImage(savedPath);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 실패", e);
            }
        }

        counselorRepository.save(counselor);
    }

}
