package com.take.take_breath.payment;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.take.take_breath._core._errors.exception.Exception400;
import com.take.take_breath._core._errors.exception.Exception500;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
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
    
    // 허용된 충전 금액 목록
    private static final List<Long> ALLOWED_AMOUNTS = List.of(
            10000L, 30000L, 50000L, 100000L
    );
    
    /**
     * 결제 준비 - merchantUid 생성 및 포트원 사전 검증
     */
    @Transactional
    public PaymentResponse.PrepareDTO prepare(Long memberId, PaymentRequest.PrepareDTO request) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));
        
        // 2. 금액 검증 (허용된 금액인지 확인)
        if (!ALLOWED_AMOUNTS.contains(request.getAmount())) {
            throw new Exception400("유효하지 않은 결제 금액입니다. 허용된 금액: " + ALLOWED_AMOUNTS);
        }
        
        // 3. merchantUid 생성
        String merchantUid = generateMerchantUid();
        
        // 4. 포트원 사전 검증 등록
        try {
            PrepareData prepareData = new PrepareData(
                    merchantUid, 
                    BigDecimal.valueOf(request.getAmount())
            );
            iamportClient.postPrepare(prepareData);
            log.info("포트원 사전 검증 등록 완료 - merchantUid: {}, amount: {}", 
                    merchantUid, request.getAmount());
        } catch (IamportResponseException | IOException e) {
            log.error("포트원 사전 검증 등록 실패: {}", e.getMessage());
            throw new Exception500("결제 준비에 실패했습니다.");
        }
        
        // 5. Payment 엔티티 생성 (PENDING 상태)
        Payment payment = Payment.builder()
                .member(member)
                .merchantUid(merchantUid)
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .orderName(request.getOrderName())
                .buyerName(request.getBuyerName())
                .buyerEmail(request.getBuyerEmail())
                .buyerTel(request.getBuyerTel())
                .build();
        
        paymentRepository.save(payment);
        
        log.info("결제 준비 완료 - merchantUid: {}, amount: {}", merchantUid, request.getAmount());
        
        return PaymentResponse.PrepareDTO.builder()
                .merchantUid(merchantUid)
                .amount(request.getAmount())
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
            
            // 포인트 적립
            Member member = payment.getMember();
            Long beforePoint = member.getPoint();
            member.setPoint(beforePoint + payment.getAmount());
            
            // 포인트 히스토리 저장
            PointHistory history = PointHistory.builder()
                    .member(member)
                    .type(PointTransactionType.CHARGE)
                    .amount(payment.getAmount())
                    .balanceAfter(member.getPoint())
                    .description("포인트 충전 - " + payment.getOrderName())
                    .build();
            
            pointHistoryRepository.save(history);
            
            log.info("결제 검증 완료 - impUid: {}, amount: {}, 포인트 적립: {}P", 
                    request.getImpUid(), payment.getAmount(), payment.getAmount());
            
            return new PaymentResponse.ResponseDTO(payment);
            
        } catch (IamportResponseException | IOException e) {
            log.error("포트원 API 호출 실패: {}", e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new Exception500("결제 검증에 실패했습니다.");
        }
    }
    
    /**
     * 내 결제 내역 조회 (페이징)
     */
    public Page<PaymentResponse.ListDTO> getMyPayments(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));
        
        Page<Payment> payments = paymentRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        
        // 메서드 참조 제거 - 람다 표현식 사용
        return payments.map(payment -> new PaymentResponse.ListDTO(payment));
    }
    
    /**
     * merchantUid 생성 (주문번호)
     */
    private String generateMerchantUid() {
        return "order_" + System.currentTimeMillis();
    }
}