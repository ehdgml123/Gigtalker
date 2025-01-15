package com.zerock.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CouponsDto {

    private Long id;  // 쿠폰 ID

    private Long code;  // 쿠폰 코드

    private String name;  // 쿠폰명

    private int discountAmount;  // 할인 금액

    private int minOrderAmount;  // 최소 주문 금액

    private LocalDate expirationDate;  // 만료일

    private String type;  // 쿠폰 종류 (예: 회원가입 쿠폰, 이벤트 쿠폰)



}
