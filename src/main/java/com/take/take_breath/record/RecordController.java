package com.take.take_breath.record;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath.record.dto.RecordRequest;
import com.take.take_breath.record.dto.RecordResponse;
import com.take.take_breath.record.dto.RecordSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    // 기록 목록 조회(페이징) - get
    // 정렬 - 날짜 - 최신순
    @GetMapping
    public ResponseEntity<?> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortType
    ) {
        return ResponseEntity.ok(ApiUtil.success(
                recordService.getRecordList(
                        page, size, sortField, sortType)));
    }

    // 기록 상세 조회 - get
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecord(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(ApiUtil.success(recordService.getRecord(id)));
    }

    // 기록 저장 - post
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveRecord(
            @RequestParam Long memberId,
            @ModelAttribute RecordSaveRequest request
    ) {
        Long recordId = record
    }

    // 기록 수정 - put

    // 기록 삭제 - delete - 로컬 데이터도 삭제

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