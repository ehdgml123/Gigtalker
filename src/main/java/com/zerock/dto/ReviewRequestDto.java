package com.zerock.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    private int rating; // 별점

    private String review; // 리뷰 내용

    private Long paymentId; // 어떤 결제에 대한 리뷰인지
}
