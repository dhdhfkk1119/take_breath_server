package com.take.take_breath.counselor;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.MemberService;
import com.take.take_breath.members.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CounselorService {
    private final CounselorRepository counselorRepository;
    private final MemberRepository memberRepository;
    private final CounselorApprovalRepository counselorApprovalRepository;
    private final MemberService memberService;

    @Transactional
    public CounselorResponse signup(CounselorRequest req) {
        Member member = memberService.signup(req.toMemberRequest());

        Counselor counselor = req.toEntity(member);

        counselorRepository.saveAndFlush(counselor);
//        counselor.setStatus(Status.PENDING);
        CounselorApproval approval = req.toApproval(counselor);
        counselorApprovalRepository.save(approval);

        return CounselorResponse.from(counselor);
    }

    // 상담사 전체 조회
    public List<CounselorResponse> findAll() {
        return counselorRepository.findAll().stream()
                .map(counselor -> CounselorResponse.from(counselor))
                .collect(Collectors.toList());
    }

    // 페이지네이션
    public Page<CounselorResponse> findAll(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return counselorRepository.findAll(pageable)
                .map(counselor -> CounselorResponse.from(counselor));
    }

    // 상담사 상세 조회
    public CounselorResponse findById(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        return CounselorResponse.from(counselor);
    }

    // 해시태그 검색
    public List<CounselorResponse> searchByHashtags(String keyword) {
        return counselorRepository.findByHashtagsContaining(keyword).stream()
                .map(counselor -> CounselorResponse.from(counselor))
                .collect(Collectors.toList());
    }
}
