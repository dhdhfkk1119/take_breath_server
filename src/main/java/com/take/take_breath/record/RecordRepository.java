package com.take.take_breath.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Page<Record> findAll(Pageable pageable);

    Page<Record> findByMemberId(Long memberId, Pageable pageable);

    Page<Record> findByMemberEmail(String email, Pageable pageable);
}