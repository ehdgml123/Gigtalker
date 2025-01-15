package com.zerock.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCouponsDto {

    private Long id;  // 사용자-쿠폰 매핑 ID

    private Long userId;  // 사용자 ID

    private CouponsDto coupon;  // 쿠폰 정보

    private String status;  // 쿠폰 상태 (예: "사용 가능", "사용 완료")

    private String issuedAt;  // 쿠폰 발급일
}
