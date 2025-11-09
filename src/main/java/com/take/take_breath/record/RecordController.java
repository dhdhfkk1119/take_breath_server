package com.take.take_breath.record;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath.record.dto.*;
import com.take.take_breath.record.dto.RecordSearchCondition.SearchType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    /**
     * 기록 목록 조회(페이징) - get
     * 정렬 - 날짜 - 최신순
     *
     * @param request
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String memberEmail = request.getAttribute("memberEmail").toString();
        Page<RecordListResponse> records = recordService.getRecordList(memberEmail, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtil.success(PageUtil.PageResponse.of(records)));
    }

    /**
     * 키워드 목록 조회(패이징) - get
     * http://localhost:8080/api/records/search?keyword=테&searchType=TITLE&hasImage=false
     * @param request
     * @param keyword
     * @param searchType
     * @param hasImage
     * @param hasAudio
     * @param hasVideo
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchWithCondition(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) Boolean hasImage,
            @RequestParam(required = false) Boolean hasAudio,
            @RequestParam(required = false) Boolean hasVideo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String memberEmail = request.getAttribute("memberEmail").toString();

        // LocalDate -> Timestamp 변환
        Timestamp startTimestamp = startDate != null
                ? Timestamp.valueOf(startDate.atStartOfDay())
                : null;
        Timestamp endTimestamp = endDate != null
                ? Timestamp.valueOf(endDate.atTime(23, 59, 59))
                : null;

        RecordSearchCondition condition = RecordSearchCondition.builder()
                .keyword(keyword)
                .searchTarget(searchType)
                .hasImage(hasImage)
                .hasAudio(hasAudio)
                .hasVideo(hasVideo)
                .startDate(startTimestamp)
                .endDate(endTimestamp)
                .build();

        Page<RecordListResponse> records = recordService.searchWithCondition(memberEmail, condition, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtil.success(PageUtil.PageResponse.of(records)));
    }


    /**
     * 기록 상세 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecord(@PathVariable(name = "id") Long id) {
        Record record = recordService.getRecord(id);
        RecordResponse response = RecordResponse.fromEntity(record);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtil.success(response));
    }

    /**
     * 기록 저장
     *
     * @param request
     * @param recordRequest
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveRecord(
            HttpServletRequest request,
            @ModelAttribute RecordSaveRequest recordRequest) {
        String email = request.getAttribute("memberEmail").toString();
        Record record = recordService.saveRecord(email, recordRequest);
        RecordResponse response = RecordResponse.fromEntity(record);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiUtil.success(response));
    }

    /**
     * 기록 수정
     *
     * @param request
     * @param id
     * @param recordRequest
     * @return
     */
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRecord(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id,
            @ModelAttribute RecordUpdateRequest recordRequest) {
        String email = request.getAttribute("memberEmail").toString();
        Record updateRecord = recordService.updateRecord(email, id, recordRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtil.success(updateRecord.getId()));
    }

    /**
     * 기록 삭제 - delete
     *
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id) {
        String email = request.getAttribute("memberEmail").toString();
        recordService.deleteRecord(email, id);

        // 일반적인 RESTAPI의 관례 - 삭제 시 not found
        // return ResponseEntity.notFound().build();

        return ResponseEntity.ok(ApiUtil.success("기록이 성공적으로 삭제되었습니다."));
    }


    // 수정된 부분 (getPdf 메서드만)

    // 기록 다운로드 기능 - pdf
    @GetMapping("/pdf/{id}")
    public ResponseEntity<?> getPdf(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id) {
        String memberEmail = request.getAttribute("memberEmail").toString();

        byte[] pdf = recordService.generatePdf(memberEmail, id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // ✅ 수정: 영문 파일명 + 타임스탐프 사용
        String timestamp = System.currentTimeMillis() + "";
        String fileName = "record_" + id + "_" + timestamp + ".pdf";
        headers.setContentDispositionFormData("attachment", fileName);

        // ✅ 추가: Content-Length 헤더 설정 (중요!)
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        /**
         * axios.get('http://localhost:8080/api/record/pdf/5', {
         *   responseType: 'blob', // ✅ 이게 중요! (binary로 받기)
         *   headers: {
         *     Authorization: `Bearer ${token}`,
         *   },
         * })
         * .then(response => {
         *   // Blob으로 변환
         *   const blob = new Blob([response.data], { type: 'application/pdf' });
         *   const url = window.URL.createObjectURL(blob);
         *
         *   // 다운로드 링크 생성
         *   const link = document.createElement('a');
         *   link.href = url;
         *   link.download = 'record.pdf';
         *   link.click();
         * });
         */
    }
}


/**
 * Page<Record> 객체
 * {
 * "content": [
 * { ... Record 데이터 ... },
 * { ... }
 * ],
 * "pageable": {
 * "pageNumber": 0,
 * "pageSize": 10,
 * ...
 * },
 * "totalPages": 5,
 * "totalElements": 42,
 * "last": false,
 * "first": true,
 * "numberOfElements": 10
 * }
 */
// 2025-10-30T10:00:00