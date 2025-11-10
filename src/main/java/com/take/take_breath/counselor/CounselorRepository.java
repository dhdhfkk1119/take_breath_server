package com.take.take_breath.counselor;

import com.take.take_breath.members.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, Long> {

    // 해시태그 검색 기능 (예: "#연애" 포함된 상담사 찾기)
    List<Counselor> findByHashtagsContaining(String keyword);
    Page<Counselor> findByStatus(Status status, Pageable pageable);
    long countByStatus(Status status);
}