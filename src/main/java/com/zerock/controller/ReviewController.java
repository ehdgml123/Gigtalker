package com.zerock.controller;

import com.zerock.dto.ReviewRequestDto;
import com.zerock.dto.ReviewResponseDto;
import com.zerock.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 리뷰 생성 API
    @PostMapping("/submit-review")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        try {
            // 리뷰 저장 서비스 호출
            reviewService.saveReview(reviewRequestDto);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품별 리뷰 조회 API
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByProduct(@PathVariable Long productId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }
}
