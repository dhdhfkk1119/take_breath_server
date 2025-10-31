package com.take.take_breath.admin.view;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCounselorService {

    private final CounselorRepository counselorRepository;

    /**
     * 승인 대기 중인 상담사 목록 조회
     */
    public List<Counselor> findPendingCounselors() {
        return counselorRepository.findByStatus(Status.PENDING);
    }

    /**
     * 전체 상담사 조회
     */
    public List<Counselor> getAllCounselors() {
        return counselorRepository.findAll();
    }

    /**
     * 상태별 상담사 조회
     */
    public List<Counselor> getCounselorsByStatus(Status status) {
        return counselorRepository.findByStatus(status);
    }

    /**
     * 상담사 승인
     */
    @Transactional
    public void approveCounselor(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        counselor.setStatus(Status.ACTIVE);
        counselorRepository.save(counselor);

        log.info("상담사 승인 완료: counselorId={}", id);
    }

    /**
     * 상담사 거절
     */
    @Transactional
    public void rejectCounselor(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        counselor.setStatus(Status.REJECTED);
        counselorRepository.save(counselor);

        log.info("상담사 거절 완료: counselorId={}", id);
    }
}