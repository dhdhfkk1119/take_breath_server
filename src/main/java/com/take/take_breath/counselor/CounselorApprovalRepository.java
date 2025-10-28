package com.take.take_breath.counselor;

import com.take.take_breath.members.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounselorApprovalRepository extends JpaRepository<CounselorApproval, Long> {
    List<CounselorApproval> findByStatus(Status status);
}
