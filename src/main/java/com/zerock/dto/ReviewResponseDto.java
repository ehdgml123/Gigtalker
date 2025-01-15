package com.zerock.dto;

import com.zerock.service.MailService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

    private Long id; // 리뷰 ID

    private int rating; // 별점

    private String review; // 리뷰 내용

    private Long paymentId; // 결제 ID

    private String productTitle; // 상품 제목

}
