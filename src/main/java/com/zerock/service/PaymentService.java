package com.zerock.service;

import com.zerock.Entity.Payment;
import com.zerock.Entity.ProductionService;
import com.zerock.Entity.Users;
import com.zerock.Repository.PaymentRepository;
import com.zerock.Repository.ProductionServiceRepository;
import com.zerock.Repository.UsersRepository;
import com.zerock.dto.PaymentRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductionServiceRepository productionServiceRepository; // 상품 정보를 조회하기 위해 추가
    private final UsersRepository usersRepository;

    // 결제 정보 저장
    @Transactional
    public Payment savePayment(PaymentRequestDto paymentRequestDto) {
        // 상품 조회
        ProductionService productionService = productionServiceRepository.findById(paymentRequestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다: " + paymentRequestDto.getProductId()));

        // 사용자 조회
        Users user = usersRepository.findById(paymentRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + paymentRequestDto.getUserId()));

        // 결제 엔티티 생성
        Payment payment = new Payment();
        payment.setUser(user); // 사용자 객체 설정
        payment.setProductionService(productionService);
        payment.setOrderNumber(paymentRequestDto.getOrderNumber());
        payment.setImpUid(paymentRequestDto.getImpUid());
        payment.setAmount(paymentRequestDto.getAmount());
        payment.setPaymentMethod(paymentRequestDto.getPaymentMethod());
        payment.setStatus("ready");

        return paymentRepository.save(payment);
    }

    // 결제 상태 업데이트
    @Transactional
    public void updatePaymentStatus(String orderNumber, String status) {
        log.info("Received request to update payment status for orderNumber: {}", orderNumber);
        Payment payment = paymentRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> {
                    log.error("Payment not found for orderNumber: {}", orderNumber);
                    return new IllegalArgumentException("해당 주문 번호에 대한 결제를 찾을 수 없습니다: " + orderNumber);
                });

        payment.setStatus(status); // 상태를 업데이트
        paymentRepository.save(payment);
        log.info("Payment status updated to: {}", status);
    }

    // 주문 번호로 결제 정보 조회
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderNumber(String orderNumber) {
        return paymentRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 번호에 대한 결제 정보를 찾을 수 없습니다: " + orderNumber));
    }

    // 아임포트 UID로 결제 정보 조회
    @Transactional(readOnly = true)
    public Payment getPaymentByImpUid(String impUid) {
        return paymentRepository.findByImpUid(impUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 아임포트 UID에 대한 결제 정보를 찾을 수 없습니다: " + impUid));
    }

    // 사용자별 구매 내역 조회
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }

    // 결제 ID로 사용자 정보 조회
    @Transactional(readOnly = true)
    public Users getUserByPaymentId(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다: " + paymentId));
        return payment.getUser(); // Lazy Loading 발생 가능
    }
}

