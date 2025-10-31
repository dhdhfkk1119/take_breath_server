package com.take.take_breath.record;

import com.take.take_breath.record.dto.RecordSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordRepositoryCustom {

    /**
     * 통합 검색 (키워드 + 날짜 + 파일 타입 필터)
     * @param email
     * @param condition
     * @param pageable
     * @return
     */
    Page<Record> searchWithCondition(String email, RecordSearchCondition condition, Pageable pageable);
}
