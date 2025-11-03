package com.take.take_breath.refund;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception500;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.payment.Payment;
import com.take.take_breath.payment.PaymentRepository;
import com.take.take_breath.payment.PaymentStatus;
import com.take.take_breath.point.PointHistory;
import com.take.take_breath.point.PointHistoryRepository;
import com.take.take_breath.point.PointTransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final IamportClient iamportClient;

    // 환불 가능 기간 (7일)
    private static final int REFUND_PERIOD_DAYS = 7;

    /**
     * 환불 요청
     * - 결제일로부터 7일 이내 미사용 포인트만 환불 가능 (FIFO 정책)
     * - 포트원에 포인트 금액만 환불 요청 (수수료 제외)
     */
    @Transactional
    public RefundResponse.DetailDTO requestRefund(Long memberId, RefundRequest.CreateDTO request) {

        // 결제 내역 조회
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new Exception400("존재하지 않는 결제 내역입니다."));

        // 본인 결제인지 확인
        if (!payment.getMember().getId().equals(memberId)) {
            throw new Exception400("본인의 결제 내역만 환불 요청할 수 있습니다.");
        }

        // 결제 완료 상태인지 확인
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new Exception400("결제 완료 상태가 아닙니다.");
        }

        // 이미 환불 요청한 내역인지 확인
        if (refundRepository.existsByPaymentId(payment.getId())) {
            throw new Exception400("이미 환불 요청한 결제입니다.");
        }

        // 결제일로부터 7일 이내인지 확인
        validateRefundPeriod(payment);

        // FIFO 방식으로 미사용 여부 확인 (부분 사용 시 환불 불가)
        validatePaymentNotUsedFifo(memberId, payment);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));

        // 현재 잔액이 전체 금액보다 적으면 환불 불가
        if (member.getPoint() < payment.getPointAmount()) {
            throw new Exception400(String.format(
                    "포인트가 부족합니다. (환불 포인트: %dP, 현재 잔액: %dP)",
                    payment.getPointAmount(), member.getPoint()
            ));
        }

        // 환불 요청 저장 (전체 금액)
        Refund refund = Refund.builder()
                .payment(payment)
                .member(member)
                .refundAmount(payment.getAmount())
                .status(RefundStatus.PENDING)
                .reason(request.getReason())
                .build();

        refundRepository.save(refund);

        log.info("환불 요청 생성 - paymentId: {}, memberId: {}, 환불액: {}원",
                payment.getId(), memberId, payment.getAmount());

        // 자동 승인 후 처리
        return processRefund(refund.getId());
    }

    /**
     * FIFO 방식으로 해당 충전건이 전혀 사용되지 않았는지 검증
     * 부분 사용된 경우에도 환불 불가
     */
    private void validatePaymentNotUsedFifo(Long memberId, Payment targetPayment) {
        // 모든 충전 내역 조회 (오래된 순)
        List<PointHistory> chargeHistories = pointHistoryRepository
                .findChargeHistoriesForFifo(memberId);

        // 모든 사용 내역 조회 (오래된 순)
        List<PointHistory> useHistories = pointHistoryRepository
                .findUseHistoriesForFifo(memberId);

        // 각 충전건별 남은 금액 계산
        Map<Long, Long> paymentRemainingMap = new HashMap<>();
        for (PointHistory charge : chargeHistories) {
            Long paymentId = charge.getPayment() != null ? charge.getPayment().getId() : null;
            if (paymentId != null) {
                // 충전 시 저장된 포인트 금액 (수수료 제외)
                paymentRemainingMap.put(paymentId, charge.getAmount());
            }
        }

        // FIFO 순서로 사용 금액 차감
        for (PointHistory use : useHistories) {
            Long usedAmount = Math.abs(use.getAmount());

            // 오래된 충전건부터 차감
            for (PointHistory charge : chargeHistories) {
                if (usedAmount <= 0) break;

                Long paymentId = charge.getPayment() != null ? charge.getPayment().getId() : null;
                if (paymentId == null) continue;

                Long remaining = paymentRemainingMap.getOrDefault(paymentId, 0L);
                if (remaining <= 0) continue;

                // 이 충전건에서 차감할 금액
                Long deductAmount = Math.min(remaining, usedAmount);
                paymentRemainingMap.put(paymentId, remaining - deductAmount);
                usedAmount -= deductAmount;

                log.debug("FIFO 차감 - paymentId: {}, 차감: {}P, 남은금액: {}P",
                        paymentId, deductAmount, paymentRemainingMap.get(paymentId));
            }
        }

        // 요청한 충전건의 남은 금액 확인
        Long chargeAmount = targetPayment.getPointAmount();
        Long remainingAmount = paymentRemainingMap.getOrDefault(targetPayment.getId(), 0L);
        Long usedAmount = chargeAmount - remainingAmount;

        // 조금이라도 사용되었으면 환불 불가
        if (usedAmount > 0) {
            throw new Exception400(String.format(
                    "이미 사용된 충전건은 환불할 수 없습니다. (충전: %dP, 사용: %dP, 남은금액: %dP)",
                    chargeAmount, usedAmount, remainingAmount
            ));
        }

        log.info("FIFO 환불 검증 통과 - paymentId: {}, 충전: {}P, 미사용 확인",
                targetPayment.getId(), chargeAmount);
    }

    /**
     * 결제일로부터 7일 이내인지 검증
     */
    private void validateRefundPeriod(Payment payment) {
        if (payment.getPaidAt() == null) {
            throw new Exception400("결제 완료 시간을 확인할 수 없습니다.");
        }

        LocalDateTime paidDate = payment.getPaidAt().toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long daysPassed = ChronoUnit.DAYS.between(paidDate, now);

        if (daysPassed > REFUND_PERIOD_DAYS) {
            throw new Exception400(String.format(
                    "환불 가능 기간(결제일로부터 %d일)이 지났습니다. (경과 일수: %d일)",
                    REFUND_PERIOD_DAYS, daysPassed
            ));
        }
    }

    /**
     * 환불 처리 (포트원 API 호출 + 포인트 차감)
     * - 포트원에 전체 금액 환불 요청
     * - 사용자 포인트는 전체 금액 차감
     */
    @Transactional
    public RefundResponse.DetailDTO processRefund(Long refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new Exception400("존재하지 않는 환불 요청입니다."));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new Exception400("처리 가능한 상태가 아닙니다.");
        }

        Payment payment = refund.getPayment();
        Member member = refund.getMember();

        try {
            // 포트원 환불 API 호출
            CancelData cancelData = new CancelData(
                    payment.getImpUid(),
                    true,
                    BigDecimal.valueOf(payment.getAmount())
            );
            cancelData.setReason(refund.getReason());

            IamportResponse<com.siot.IamportRestClient.response.Payment> cancelResponse
                    = iamportClient.cancelPaymentByImpUid(cancelData);

            if (cancelResponse.getCode() != 0) {
                throw new Exception500("포트원 환불 처리 실패: " + cancelResponse.getMessage());
            }

            // 포인트 차감 (전체 금액)
            Long beforePoint = member.getPoint();
            member.setPoint(beforePoint - payment.getPointAmount());

            // 포인트 히스토리 저장
            PointHistory history = PointHistory.builder()
                    .member(member)
                    .payment(payment)
                    .type(PointTransactionType.REFUND)
                    .amount(-payment.getPointAmount())
                    .balanceAfter(member.getPoint())
                    .description("포인트 환불 - " + payment.getOrderName())
                    .build();

            pointHistoryRepository.save(history);

            // 환불 완료 처리
            String impCancelUid = cancelResponse.getResponse().getCancelHistory()[0].getPgTid();
            refund.completeRefund(impCancelUid);

            // 결제 상태 변경
            payment.setStatus(PaymentStatus.CANCELLED);

            log.info("환불 처리 완료 - refundId: {}, 환불액: {}원, 차감포인트: {}P",
                    refund.getId(), refund.getRefundAmount(), payment.getPointAmount());

            return new RefundResponse.DetailDTO(refund);

        } catch (IamportResponseException | IOException e) {
            log.error("포트원 환불 API 호출 실패: {}", e.getMessage());
            refund.setStatus(RefundStatus.REJECTED);
            throw new Exception500("환불 처리에 실패했습니다.");
        }
    }

    /**
     * 내 환불 내역 조회
     */
    public Page<RefundResponse.ListDTO> getMyRefunds(Long memberId, Pageable pageable) {
        Page<Refund> refunds = refundRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return refunds.map(refund -> new RefundResponse.ListDTO(refund));
    }

    /**
     * 환불 상세 조회
     */
    public RefundResponse.DetailDTO getRefundDetail(Long memberId, Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new Exception400("존재하지 않는 환불 내역입니다."));

        if (!refund.getMember().getId().equals(memberId)) {
            throw new Exception400("본인의 환불 내역만 조회할 수 있습니다.");
        }

        return new RefundResponse.DetailDTO(refund);
    }
}