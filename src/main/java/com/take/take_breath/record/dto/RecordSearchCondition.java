package com.take.take_breath.record.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Record 검색 조건 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordSearchCondition {

    /**
     * 키워드 검색어
     */
    private String keyword;

    /**
     * 검색 타입
     */
    private SearchType searchTarget;

    /**
     * 이미지가 있는 기록만 조회
     */
    private Boolean hasImage;

    /**
     * 오디오가 있는 기록만 조회
     */
    private Boolean hasAudio;

    /**
     * 비디오가 있는 기록만 조회
     */
    private Boolean hasVideo;

    /**
     * 검색 시작 날짜
     */
    private Timestamp startDate;

    /**
     * 검색 종료 날짜
     */
    private Timestamp endDate;

    public enum SearchType {
        TITLE, CONTENT, ALL
    }
}