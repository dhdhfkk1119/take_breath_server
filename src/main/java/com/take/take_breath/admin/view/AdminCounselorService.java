package com.take.take_breath.admin.view;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._utils.PageUtil.PageResponse;
import com.take.take_breath.admin.view.dto.CounselorListResponse;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCounselorService {

    private final CounselorRepository counselorRepository;

    // ✅ 전체 상담사 (페이징)
    public PageResponse<CounselorListResponse> getAllCounselors(Pageable pageable) {
        Page<Counselor> page = counselorRepository.findAll(pageable);
        List<CounselorListResponse> content = page.getContent().stream()
                .map(c -> new CounselorListResponse(c))
                .collect(Collectors.toList());
        return PageResponse.of(page, content);
    }

    // ✅ 상태별 상담사 (페이징)
    public PageResponse<CounselorListResponse> getCounselorsByStatus(Status status, Pageable pageable) {
        Page<Counselor> page = counselorRepository.findByStatus(status, pageable);
        List<CounselorListResponse> content = page.getContent().stream()
                .map(c -> new CounselorListResponse(c))
                .collect(Collectors.toList());
        return PageResponse.of(page, content);
    }

    @Transactional
    public void approveCounselor(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));
        counselor.setStatus(Status.ACTIVE);
        counselorRepository.save(counselor);
        log.info("상담사 승인 완료: counselorId={}", id);
    }

    @Transactional
    public void rejectCounselor(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));
        counselor.setStatus(Status.REJECTED);
        counselorRepository.save(counselor);
        log.info("상담사 거절 완료: counselorId={}", id);
    }
}
