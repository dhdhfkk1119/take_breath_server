package com.take.take_breath.admin;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorApproval;
import com.take.take_breath.counselor.CounselorApprovalRepository;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CounselorRepository counselorRepository;
    private final CounselorApprovalRepository counselorApprovalRepository;

    // 승인 대기 중 상담사 목록
    public List<CounselorApproval> getPendingApprovals() {
        return counselorApprovalRepository.findByStatus(Status.PENDING);
    }

    // 상담사 승인
    @Transactional
    public void approveCounselor(Long approvalId) {
        CounselorApproval approval = counselorApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("승인 요청을 찾을 수 없습니다."));
        Counselor counselor = approval.getCounselor();
        Member member = counselor.getMember();

        counselor.setStatus(Status.ACTIVE);
        member.setStatus(Status.ACTIVE);

        approval.setStatus(Status.ACTIVE);
        approval.setReason(null);

        counselorRepository.save(counselor);
        counselorApprovalRepository.save(approval);
    }

    // 상담사 거절
    @Transactional
    public void rejectCounselor(Long approvalId, String reason) {
        CounselorApproval approval = counselorApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("승인 요청을 찾을 수 없습니다."));
        Counselor counselor = approval.getCounselor();
        Member member = counselor.getMember();

        counselor.setStatus(Status.REJECTED);
        member.setStatus(Status.ACTIVE);

        approval.setStatus(Status.REJECTED);
        approval.setReason(reason);

        counselorRepository.save(counselor);
        counselorApprovalRepository.save(approval);
    }
}