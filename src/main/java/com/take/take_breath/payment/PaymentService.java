package com.take.take_breath.payment;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception500;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.payment.fee.FeeStrategy;
import com.take.take_breath.payment.fee.FeeStrategyFactory;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final IamportClient iamportClient;
    private final FeeStrategyFactory feeStrategyFactory;

    // 회원 수수료 포함 결제 금액 (10%)
    private Long calculateTotalAmount(Long pointAmount) {
        FeeStrategy strategy = feeStrategyFactory.getDefaultStrategy();
        Long feeAmount = strategy.calculateFee(pointAmount, Role.USER, "CHARGE");
        return pointAmount + feeAmount;
    }

    // 수수료 금액 (10%)
    private Long calculateFeeAmount(Long pointAmount) {
        FeeStrategy strategy = feeStrategyFactory.getDefaultStrategy();
        return strategy.calculateFee(pointAmount, Role.USER, "CHARGE");
    }

    // 상담사 출금 시 수수료 (30%)
    private Long calculateWithdrawFee(Long pointAmount) {
        FeeStrategy strategy = feeStrategyFactory.getDefaultStrategy();
        return strategy.calculateFee(pointAmount, Role.COUNSELOR, "WITHDRAW"); // 30%
    }

    // 허용된 충전 금액 목록
    private static final List<Long> ALLOWED_AMOUNTS = List.of(
            5000L, 10000L, 30000L, 50000L, 100000L
    );

    /**
     * 결제 준비 - 주문번호 생성 및 포트원 사전 검증
     */
    @Transactional
    public PaymentResponse.PrepareDTO prepare(Long memberId, PaymentRequest.PrepareDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));

        // 1. 포인트 금액 검증 (허용된 금액인지 확인)
        Long pointAmount = request.getAmount();
        if (!ALLOWED_AMOUNTS.contains(pointAmount)) {
            throw new Exception400("유효하지 않은 포인트 금액입니다. 허용된 금액: " + ALLOWED_AMOUNTS);
        }

        // 2. 수수료 계산
        FeeStrategy feeStrategy = feeStrategyFactory.getDefaultStrategy();
        Long feeAmount = feeStrategy.calculateFee(pointAmount, Role.USER, "CHARGE");
        Long totalAmount = calculateTotalAmount(pointAmount);

        // 3. 주문번호 생성
        String merchantUid = generateMerchantUid();

        // 4. 포트원 사전 검증 등록 (실제 결제될 금액으로)
        try {
            PrepareData prepareData = new PrepareData(
                    merchantUid,
                    BigDecimal.valueOf(totalAmount)  // 수수료 포함 금액
            );
            iamportClient.postPrepare(prepareData);
            log.info("포트원 사전 검증 등록 완료 - merchantUid: {}, pointAmount: {}P, feeAmount: {}원, totalAmount: {}원",
                    merchantUid, pointAmount, feeAmount, totalAmount);
        } catch (IamportResponseException | IOException e) {
            log.error("포트원 사전 검증 등록 실패: {}", e.getMessage());
            throw new Exception500("결제 준비에 실패했습니다.");
        }

        // 5. Payment 엔티티 생성 (PENDING 상태)
        Payment payment = Payment.builder()
                .member(member)
                .merchantUid(merchantUid)
                .pointAmount(pointAmount)             // 실제 적립될 포인트
                .feeAmount(feeAmount)                 // 수수료
                .feeRate(feeStrategy.getFeeRate())    // 수수료율
                .amount(totalAmount)                  // 총 결제 금액
                .status(PaymentStatus.PENDING)
                .orderName(request.getOrderName())
                .buyerName(request.getBuyerName())
                .buyerEmail(request.getBuyerEmail())
                .buyerTel(request.getBuyerTel())
                .build();

        paymentRepository.save(payment);

        log.info("결제 준비 완료 - merchantUid: {}, pointAmount: {}P, totalAmount: {}원",
                merchantUid, pointAmount, totalAmount);

        return PaymentResponse.PrepareDTO.builder()
                .merchantUid(merchantUid)
                .amount(totalAmount)           // 실제 결제 금액
                .pointAmount(pointAmount)      // 적립될 포인트
                .feeAmount(feeAmount)          // 수수료
                .orderName(request.getOrderName())
                .buyerName(request.getBuyerName())
                .buyerEmail(request.getBuyerEmail())
                .buyerTel(request.getBuyerTel())
                .build();
    }

    /**
     * 결제 검증 및 포인트 적립
     */
    @Transactional
    public PaymentResponse.ResponseDTO verify(Long memberId, PaymentRequest.VerifyDTO request) {
        // Payment 조회
        Payment payment = paymentRepository.findByMerchantUid(request.getMerchantUid())
                .orElseThrow(() -> new Exception400("존재하지 않는 결제 정보입니다."));

        // 본인 결제인지 확인
        if (!payment.getMember().getId().equals(memberId)) {
            throw new Exception400("본인의 결제만 처리할 수 있습니다.");
        }

        // 이미 처리된 결제인지 확인
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new Exception400("이미 처리된 결제입니다.");
        }

        try {
            // 포트원 API로 결제 정보 조회
            IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse
                = iamportClient.paymentByImpUid(request.getImpUid());

            com.siot.IamportRestClient.response.Payment iamportPayment = iamportResponse.getResponse();

            if (iamportPayment == null) {
                throw new Exception400("포트원에서 결제 정보를 찾을 수 없습니다.");
            }

            // 결제 금액 검증
            if (!iamportPayment.getAmount().equals(BigDecimal.valueOf(payment.getAmount()))) {
                throw new Exception400("결제 금액이 일치하지 않습니다.");
            }

            // 결제 상태 확인
            if (!"paid".equals(iamportPayment.getStatus())) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new Exception400("결제가 완료되지 않았습니다.");
            }

            // 결제 완료 처리
            payment.completePay(request.getImpUid(), iamportPayment.getPayMethod());

            // 포인트 적립 (수수료 제외한 금액만 적립)
            Member member = payment.getMember();
            Long beforePoint = member.getPoint();
            member.setPoint(beforePoint + payment.getPointAmount());

            // 포인트 히스토리 저장
            PointHistory history = PointHistory.builder()
                    .member(member)
                    .payment(payment)
                    .type(PointTransactionType.CHARGE)
                    .amount(payment.getPointAmount())  // 수수료 제외한 포인트만 기록
                    .balanceAfter(member.getPoint())
                    .description("포인트 충전 - " + payment.getOrderName())
                    .build();

            pointHistoryRepository.save(history);

            log.info("결제 검증 완료 - impUid: {}, totalAmount: {}원, pointAmount: {}P, feeAmount: {}원",
                    request.getImpUid(), payment.getAmount(), payment.getPointAmount(), payment.getFeeAmount());

            return new PaymentResponse.ResponseDTO(payment);

        } catch (IamportResponseException | IOException e) {
            log.error("포트원 API 호출 실패: {}", e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new Exception500("결제 검증에 실패했습니다.");
        }
    }

    /**
     * 내 결제 내역 조회
     */
    public Page<PaymentResponse.ListDTO> getMyPayments(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));

        Page<Payment> payments = paymentRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        return payments.map(payment -> new PaymentResponse.ListDTO(payment));
    }

    /**
     * 관리자용 수수료 통계 조회
     */
    public PaymentResponse.AdminFeeStatsDTO getAdminFeeStats() {
        Long totalFee = paymentRepository.getTotalFeeAmount();
        Long count = paymentRepository.countPaidPayments();
        Long totalAmount = paymentRepository.getTotalPaymentAmount();

        return PaymentResponse.AdminFeeStatsDTO.builder()
                .totalFeeAmount(totalFee != null ? totalFee : 0L)
                .totalPaymentCount(count != null ? count : 0L)
                .totalPaymentAmount(totalAmount != null ? totalAmount : 0L)
                .build();
    }

    /**
     * merchantUid 주문번호 생성
     */
    private String generateMerchantUid() {
        return "order_" + System.currentTimeMillis();
    }

    // 월별 조회
    public List<PaymentResponse.AdminFeeStatsDTO> getMonthlyFeeStats() {
        List<Object[]> results = paymentRepository.getMonthlyFeeStats();

        return results.stream()
                .map(row -> PaymentResponse.AdminFeeStatsDTO.builder()
                        .month((String) row[0])
                        .totalFeeAmount(((Number) row[1]).longValue())
                        .totalPaymentAmount(((Number) row[2]).longValue())
                        .totalPaymentCount(((Number) row[3]).longValue())
                        .build())
                .toList();
    }
}