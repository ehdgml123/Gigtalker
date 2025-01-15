package com.zerock.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCoupons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 사용자와의 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupons coupon; // 쿠폰과의 관계

    @Column(nullable = false)
    private String status; // 쿠폰 상태 (예: "사용 가능", "사용 완료")

    @Column(nullable = false)
    private LocalDateTime issuedAt; // 쿠폰 발급일

}
