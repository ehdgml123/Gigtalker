package com.zerock.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentRequestDto {

    private Long id; // 고유 ID

    private Long userId; // 결제를 한 사용자 ID

    private Long productId; // 결제된 상품 ID

    private String orderNumber; // 주문 번호 (아임포트 Merchant UID)

    private String impUid; // 아임포트 결제 UID

    private Long amount; // 결제 금액

    private String status; // 결제 상태 (e.g., paid, cancelled)

    private String paymentMethod; // 결제 방법 (CARD, BANK, VBANK 등)




}
