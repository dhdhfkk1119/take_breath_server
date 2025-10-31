package com.take.take_breath.record;

import com.take.take_breath._core._exception.Exception500;
import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath.record.dto.RecordListResponse;
import com.take.take_breath.record.dto.RecordResponse;
import com.take.take_breath.record.dto.RecordSaveRequest;
import com.take.take_breath.record.dto.RecordUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    /**
     * 기록 목록 조회(페이징) - get
     * 정렬 - 날짜 - 최신순
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
     * 기록 상세 조회
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
     * @param request
     * @param id
     * @param recordRequest
     * @return
     */
    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    // 기록 삭제 - delete - 로컬 데이터도 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteRecord(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id) {
        String email = request.getAttribute("memberEmail").toString();
        recordService.deleteRecord(email, id);

        // 일반적인 RESTAPI의 관례 - 삭제 시 not found
        // return ResponseEntity.notFound().build();

        return ResponseEntity.ok(ApiUtil.success("기록이 성공적으로 삭제되었습니다."));
    }

    // 키워드 검색 - 제목, 내용, 날짜 - queryDSL

    // 자료가 있는것만 조회 - 사진, 오디오

    // 기록 다운로드 기능 - pdf
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