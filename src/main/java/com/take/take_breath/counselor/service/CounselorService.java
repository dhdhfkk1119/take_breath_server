package com.take.take_breath.counselor.service;

import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.counselor.entity.Counselor;
import com.take.take_breath.counselor.repository.CounselorRepository;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CounselorService {
    private final CounselorRepository counselorRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CounselorResponse signup(Long memberId, CounselorRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        Counselor counselor = req.toEntity(member);
        counselorRepository.save(counselor);

        return CounselorResponse.from(counselor);
    }

    // 상담사 전체 조회
    public List<CounselorResponse> findAll() {
        return counselorRepository.findAll().stream()
                .map(counselor -> CounselorResponse.from(counselor))
                .collect(Collectors.toList());
    }

    // 상담사 상세 조회
    public CounselorResponse findById(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));

        return CounselorResponse.from(counselor);
    }

    // 해시태그 검색
    public List<CounselorResponse> searchByHashtags(String keyword) {
        return counselorRepository.findByHashtagsContaining(keyword).stream()
                .map(counselor -> CounselorResponse.from(counselor))
                .collect(Collectors.toList());
    }

}
