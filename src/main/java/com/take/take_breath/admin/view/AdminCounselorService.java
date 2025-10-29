package com.take.take_breath.admin.view;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCounselorService {

    private final CounselorRepository counselorRepository;

    public List<Counselor> findPendingCounselors() {
        return counselorRepository.findByStatus(Status.PENDING);
    }

    @Transactional
    public void approveCounselor(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        counselor.setStatus(Status.ACTIVE);
        counselorRepository.save(counselor);
    }

    @Transactional
    public void rejectCounselor(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new Exception400("상담사를 찾을 수 없습니다."));

        counselor.setStatus(Status.REJECTED);
        counselorRepository.save(counselor);
    }
}
