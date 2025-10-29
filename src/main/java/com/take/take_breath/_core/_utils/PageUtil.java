package com.take.take_breath._core._utils;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

public class PageUtil {
    /**
     * 페이징 응답을 위한 공통 DTO
     * Spring Data Page 객체를 간소화하여 필요한 정보만 전달
     */
    @Data
    public static class PageResponse<T> {
        private List<T> content;          // 실제 데이터
        private int pageNumber;           // 현재 페이지 번호 (0부터 시작)
        private int pageSize;             // 페이지당 항목 수
        private long totalElements;       // 전체 항목 수
        private int totalPages;           // 전체 페이지 수
        private boolean isFirst;          // 첫 페이지 여부
        private boolean isLast;           // 마지막 페이지 여부

        /**
         * Spring Data Page 객체를 PageResponse로 변환
         */
        public static <T> PageResponse<T> of(Page<T> page) {
            PageResponse<T> response = new PageResponse<>();
            response.content = page.getContent();
            response.pageNumber = page.getNumber();
            response.pageSize = page.getSize();
            response.totalElements = page.getTotalElements();
            response.totalPages = page.getTotalPages();
            response.isFirst = page.isFirst();
            response.isLast = page.isLast();
            return response;
        }

        /**
         * 데이터를 변환하여 PageResponse를 생성
         */
        public static <T> PageResponse<T> of(Page<?> page, List<T> content) {
            PageResponse<T> response = new PageResponse<>();
            response.content = content;
            response.pageNumber = page.getNumber();
            response.pageSize = page.getSize();
            response.totalElements = page.getTotalElements();
            response.totalPages = page.getTotalPages();
            response.isFirst = page.isFirst();
            response.isLast = page.isLast();
            return response;
        }
    }
}

/*
직군별 업스킬 핵시
     - 함수호출-툴링 설계, 프롬프트 안전장치, 로그로 품질평가

프롬프트 운여 템플릿
    - rtf - role(역할), task(과업), facts(사실), output(형식), constraints(제약)


*/