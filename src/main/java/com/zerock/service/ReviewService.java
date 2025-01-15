package com.zerock.service;

import com.zerock.Entity.Payment;
import com.zerock.Entity.Review;
import com.zerock.Repository.PaymentRepository;
import com.zerock.Repository.ReviewRepository;
import com.zerock.dto.ReviewRequestDto;
import com.zerock.dto.ReviewResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // 리뷰 저장
    public boolean saveReview(ReviewRequestDto dto) {
        // 결제 정보 조회
        Payment payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        // 결제가 완료 상태인지 확인
        if (!"paid".equals(payment.getStatus())) {
            throw new IllegalStateException("결제가 완료되지 않았습니다.");
        }

        // 이미 리뷰가 작성된 경우
        if (payment.getReview() != null) {
            throw new IllegalStateException("이미 리뷰가 작성되었습니다.");
        }

        // 리뷰 생성 및 저장
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setReview(dto.getReview());
        review.setPayment(payment);

        reviewRepository.save(review);
        return true;
    }

    // 상품별 리뷰 조회
    public List<ReviewResponseDto> getReviewsByProduct(Long productId) {
        return reviewRepository.findByPayment_ProductionService_Id(productId)
                .stream()
                .map(review -> {
                    ReviewResponseDto dto = new ReviewResponseDto();
                    dto.setId(review.getId());
                    dto.setRating(review.getRating());
                    dto.setReview(review.getReview());
                    dto.setPaymentId(review.getPayment().getId());
                    dto.setProductTitle(review.getPayment().getProductionService().getTitle());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    /* 특정 상품 ID에 대한 리뷰 리스트 가져오기 (리뷰 리스트)*/
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByPayment_ProductionService_Id(productId);
    }

}
