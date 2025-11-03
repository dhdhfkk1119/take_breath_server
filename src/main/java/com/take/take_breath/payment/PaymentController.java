package com.take.take_breath.payment;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Role;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 준비
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/prepare")
    public ResponseEntity<ApiUtil.ApiResult<PaymentResponse.PrepareDTO>> prepare(
            @Valid @RequestBody PaymentRequest.PrepareDTO request,
            HttpServletRequest httpRequest) {

        Long memberId = (Long) httpRequest.getAttribute("memberId");
        PaymentResponse.PrepareDTO response = paymentService.prepare(memberId, request);

        log.info("[결제 준비] memberId={}, merchantUid={}, amount={}",
                memberId, response.getMerchantUid(), response.getAmount());

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 결제 검증 및 포인트 적립
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/verify")
    public ResponseEntity<ApiUtil.ApiResult<PaymentResponse.ResponseDTO>> verify(
            @Valid @RequestBody PaymentRequest.VerifyDTO request,
            HttpServletRequest httpRequest) {

        Long memberId = (Long) httpRequest.getAttribute("memberId");
        PaymentResponse.ResponseDTO response = paymentService.verify(memberId, request);

        log.info("[결제 검증 완료] memberId={}, impUid={}, amount={}",
                memberId, response.getImpUid(), response.getAmount());

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 내 결제 내역 조회 (페이징)
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/mine")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<PaymentResponse.ListDTO>>> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Long memberId = (Long) httpRequest.getAttribute("memberId");
        Pageable pageable = PageRequest.of(page, size);

        Page<PaymentResponse.ListDTO> payments = paymentService.getMyPayments(memberId, pageable);
        PageUtil.PageResponse<PaymentResponse.ListDTO> response = PageUtil.PageResponse.of(payments);

        log.info("[결제 내역 조회] memberId={}, page={}, size={}, totalElements={}",
                memberId, page, size, response.getTotalElements());

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 관리자용 수수료 통계 조회
     */
    @Auth(roles = {Role.ADMIN}, statuses = {Status.ACTIVE})
    @GetMapping("/admin/fee-stats")
    public ResponseEntity<ApiUtil.ApiResult<PaymentResponse.AdminFeeStatsDTO>> getFeeStats() {
        PaymentResponse.AdminFeeStatsDTO stats = paymentService.getAdminFeeStats();

        log.info("[관리자 수수료 통계 조회] totalFee={}, count={}",
                stats.getTotalFeeAmount(), stats.getTotalPaymentCount());

        return ResponseEntity.ok(ApiUtil.success(stats));
    }
}