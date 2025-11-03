package com.take.take_breath.refund;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {
    
    private final RefundService refundService;
    
    /**
     * 환불 요청
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping
    public ResponseEntity<ApiUtil.ApiResult<RefundResponse.DetailDTO>> requestRefund(
            @Valid @RequestBody RefundRequest.CreateDTO request,
            HttpServletRequest httpRequest) {
        
        Long memberId = (Long) httpRequest.getAttribute("memberId");
        RefundResponse.DetailDTO response = refundService.requestRefund(memberId, request);
        
        log.info("[환불 요청] memberId={}, paymentId={}, refundAmount={}",
                memberId, response.getPaymentId(), response.getRefundAmount());
        
        return ResponseEntity.ok(ApiUtil.success(response));
    }
    
    /**
     * 내 환불 내역 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/mine")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<RefundResponse.ListDTO>>> getMyRefunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        
        Long memberId = (Long) httpRequest.getAttribute("memberId");
        Pageable pageable = PageRequest.of(page, size);
        
        Page<RefundResponse.ListDTO> refunds = refundService.getMyRefunds(memberId, pageable);
        PageUtil.PageResponse<RefundResponse.ListDTO> response = PageUtil.PageResponse.of(refunds);
        
        log.info("[환불 내역 조회] memberId={}, page={}, size={}, totalElements={}",
                memberId, page, size, response.getTotalElements());
        
        return ResponseEntity.ok(ApiUtil.success(response));
    }
    
    /**
     * 환불 상세 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/{refundId}")
    public ResponseEntity<ApiUtil.ApiResult<RefundResponse.DetailDTO>> getRefundDetail(
            @PathVariable Long refundId,
            HttpServletRequest httpRequest) {
        
        Long memberId = (Long) httpRequest.getAttribute("memberId");
        RefundResponse.DetailDTO response = refundService.getRefundDetail(memberId, refundId);
        
        log.info("[환불 상세 조회] memberId={}, refundId={}, status={}",
                memberId, refundId, response.getStatus());
        
        return ResponseEntity.ok(ApiUtil.success(response));
    }
}