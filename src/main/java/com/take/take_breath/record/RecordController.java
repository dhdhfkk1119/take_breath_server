package com.take.take_breath.record;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
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

    @GetMapping
    @Auth(statuses = {Status.ACTIVE})
    public ResponseEntity<?> getRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean hasImage,    // ✅ 추가
            @RequestParam(required = false) Boolean hasAudio,    // ✅ 추가
            @RequestParam(required = false) Boolean hasVideo) {  // ✅ 추가
        String memberEmail = request.getAttribute("memberEmail").toString();

        // ✅ 필터 파라미터가 있으면 search API 사용
        if (hasImage != null || hasAudio != null || hasVideo != null) {
            RecordSearchCondition condition = RecordSearchCondition.builder()
                    .hasImage(hasImage)
                    .hasAudio(hasAudio)
                    .hasVideo(hasVideo)
                    .build();

            Page<RecordListResponse> records = recordService.searchWithCondition(
                    memberEmail, condition, page, size);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiUtil.success(PageUtil.PageResponse.of(records)));
        }

        // 필터가 없으면 기존대로 전체 조회
        Page<RecordListResponse> records = recordService.getRecordList(memberEmail, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtil.success(PageUtil.PageResponse.of(records)));
    }

    @GetMapping("/search")
    @Auth(statuses = {Status.ACTIVE})
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

    @GetMapping("/{id}")
    @Auth(statuses = {Status.ACTIVE})
    public ResponseEntity<?> getRecord(@PathVariable(name = "id") Long id) {
        Record record = recordService.getRecord(id);
        RecordResponse response = RecordResponse.fromEntity(record);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtil.success(response));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auth(statuses = {Status.ACTIVE})
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

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auth(statuses = {Status.ACTIVE})
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

    @DeleteMapping("/{id}")
    @Auth(statuses = {Status.ACTIVE})
    public ResponseEntity<?> deleteRecord(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id) {
        String email = request.getAttribute("memberEmail").toString();
        recordService.deleteRecord(email, id);
        return ResponseEntity.ok(ApiUtil.success("기록이 성공적으로 삭제되었습니다."));
    }

    @GetMapping("/pdf/{id}")
    @Auth(statuses = {Status.ACTIVE})
    public ResponseEntity<?> getPdf(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id) {
        String memberEmail = request.getAttribute("memberEmail").toString();
        byte[] pdf = recordService.generatePdf(memberEmail, id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String timestamp = System.currentTimeMillis() + "";
        String fileName = "record_" + id + "_" + timestamp + ".pdf";
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}