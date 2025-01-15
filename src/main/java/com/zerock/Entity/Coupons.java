package com.zerock.Entity;

import com.zerock.controller.SmsController;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coupons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID 컬럼

    @Column(nullable = false, unique = true)
    private Long code; // CODE 컬럼 (NUMBER)

    @Column(nullable = false)
    private String name; // NAME 컬럼

    @Column(name = "DISCOUNT_AMOUNT", nullable = false)
    private int discountAmount; // DISCOUNT_AMOUNT 컬럼

    @Column(name = "MIN_ORDER_AMOUNT", nullable = false)
    private int minOrderAmount; // MIN_ORDER_AMOUNT 컬럼

    @Column(name = "COUPON_EXPIRATION_DATE", nullable = false)
    private LocalDate couponExpirationDate; // COUPON_EXPIRATION_DATE 컬럼

    @Column(nullable = false)
    private String type;  // 쿠폰 종류 (예: 회원가입 쿠폰, 이벤트 쿠폰)
}

