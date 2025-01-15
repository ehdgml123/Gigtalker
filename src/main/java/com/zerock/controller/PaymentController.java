package com.zerock.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.zerock.Entity.Payment;
import com.zerock.Entity.Users;
import com.zerock.dto.PaymentRequestDto;
import com.zerock.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private IamportClient iamportClient;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String apiSecret;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }

    /*
     * 결제 정보 생성 paymentRequestDto 클라이언트로부터 전달된 결제 요청 DTO
     * 저장된 Payment 엔티티
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@Validated @RequestBody PaymentRequestDto paymentRequestDto) {
        try {
            log.info("결제 저장 요청: {}", paymentRequestDto);

            Payment payment = paymentService.savePayment(paymentRequestDto);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("결제 저장 실패", e);
            return ResponseEntity.status(500).body("결제 저장 실패: " + e.getMessage());
        }
    }

    /*
     * 결제 검증 impUid 아임포트 결제 UID
     * @return 검증 결과
     */
    @PostMapping("/validation/{impUid}")
    public ResponseEntity<?> validatePayment(@PathVariable String impUid) {
        try {
            log.info("결제 검증 요청: impUid={}", impUid);

            // 아임포트 API 호출
            IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.paymentByImpUid(impUid);

            // 검증 성공 여부 확인
            if (response.getResponse() != null && "paid".equals(response.getResponse().getStatus())) {
                log.info("결제 검증 성공: orderNumber={}", response.getResponse().getMerchantUid());

                // 결제 상태 업데이트
                paymentService.updatePaymentStatus(response.getResponse().getMerchantUid(), "paid");

                return ResponseEntity.ok(response.getResponse());
            } else {
                log.error("결제 검증 실패: 상태 불일치");
                return ResponseEntity.badRequest().body("결제 상태 불일치");
            }
        } catch (Exception e) {
            log.error("결제 검증 실패", e);
            return ResponseEntity.status(500).body("결제 검증 중 오류 발생");
        }
    }

    /* 아임포트 UID로 결제 정보 조회 */
    @GetMapping("/payment-user/{paymentId}")
    public ResponseEntity<?> getUserByPaymentId(@PathVariable Long paymentId) {
        try {
            Users user = paymentService.getUserByPaymentId(paymentId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("사용자 조회 실패", e);
            return ResponseEntity.status(500).body("사용자 조회 실패: " + e.getMessage());
        }
    }

    /* 결제 상태 업데이트 */
    @PostMapping("/update-status")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> request) {
        String orderNumber = request.get("orderNumber");
        String status = request.get("status");

        if (orderNumber == null || status == null) {
            log.error("Invalid request: orderNumber or status is null");
            return ResponseEntity.badRequest().body("Invalid request data: orderNumber and status are required");
        }

        try {
            log.info("결제 상태 업데이트 요청: orderNumber={}, status={}", orderNumber, status);
            paymentService.updatePaymentStatus(orderNumber, status);
            log.info("결제 상태 업데이트 성공: orderNumber={}, status={}", orderNumber, status);
            return ResponseEntity.ok("결제 상태 업데이트 성공");
        } catch (Exception e) {
            log.error("결제 상태 업데이트 실패", e);
            return ResponseEntity.status(500).body("결제 상태 업데이트 실패: " + e.getMessage());
        }
    }
}
