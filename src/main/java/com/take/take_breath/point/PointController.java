package com.take.take_breath.point;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {
    
    private final PointService pointHistoryService;
    
    /**
     * 내 포인트 잔액 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/balance")
    public ResponseEntity<ApiUtil.ApiResult<PointResponse.BalanceDTO>> getBalance(
            HttpServletRequest request) {
        
        Long memberId = (Long) request.getAttribute("memberId");
        PointResponse.BalanceDTO response = pointHistoryService.getBalance(memberId);
        
        log.info("[포인트 잔액 조회] memberId={}, point={}P", memberId, response.getPoint());
        
        return ResponseEntity.ok(ApiUtil.success(response));
    }
    
    /**
     * 내 포인트 히스토리 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/history")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<PointResponse.HistoryDTO>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        
        Long memberId = (Long) request.getAttribute("memberId");
        Pageable pageable = PageRequest.of(page, size);
        
        Page<PointResponse.HistoryDTO> histories = pointHistoryService.getHistory(memberId, pageable);
        PageUtil.PageResponse<PointResponse.HistoryDTO> response = PageUtil.PageResponse.of(histories);
        
        log.info("[포인트 히스토리 조회] memberId={}, page={}, size={}, totalElements={}", 
                memberId, page, size, response.getTotalElements());
        
        return ResponseEntity.ok(ApiUtil.success(response));
    }
}