package com.zerock.controller;

import com.zerock.Entity.ProductionService;
import com.zerock.Entity.Review;
import com.zerock.Entity.Users;
import com.zerock.service.ProductionServiceService;
import com.zerock.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class productDetailController {

    private final ProductionServiceService productionServiceService;
    private final ReviewService reviewService;

    @GetMapping("/Detailvideo/{id}")
    public String productDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
        // 상품 정보 가져오기
        ProductionService product = productionServiceService.getProductById(id);

        Users currentUser = (Users) request.getSession().getAttribute("user");

        if (product == null) {
            // 상품이 없는 경우 404 페이지로 이동
            return "error/404";
        }

        // 상품 리뷰 데이터 가져오기
        List<Review> reviews = reviewService.getReviewsByProductId(id);

        // 별점별 리뷰 개수 계산 (1~5의 기본값 포함)
        Map<Integer, Long> ratingStats = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingStats.put(i, 0L); // 초기값 설정
        }
        reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()))
                .forEach(ratingStats::put); // 실제 데이터로 값 업데이트

        // 총 리뷰 개수 계산
        long totalReviews = reviews.size();

        // 평균 평점 계산 (리뷰가 없을 경우 기본값 0.0)
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // 평균 평점을 정수로 변환 (소수점 아래는 내림 처리)
        int roundedAverageRating = (int) Math.floor(averageRating);

        // progressBarWidths 계산
        Map<Integer, String> progressBarWidths = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : ratingStats.entrySet()) {
            int rating = entry.getKey();
            long count = entry.getValue();
            String width = totalReviews > 0 ? (count * 100 / totalReviews) + "%" : "0%";
            progressBarWidths.put(rating, width);
        }

        log.info("Rating Stats: {}", ratingStats);
        log.info("Total Reviews: {}", totalReviews);
        log.info("Progress Bar Widths: {}", progressBarWidths);

        model.addAttribute("userName", currentUser.getName());

        // 모델에 데이터 추가
        model.addAttribute("product", product); // 상품 정보
        model.addAttribute("reviews", reviews); // 리뷰 데이터
        model.addAttribute("ratingStats", ratingStats); // 별점 통계
        model.addAttribute("totalReviews", totalReviews); // 리뷰 총 개수
        model.addAttribute("averageRating", averageRating); // 평균 평점
        model.addAttribute("roundedAverageRating", roundedAverageRating); // 정수화된 평균 평점
        model.addAttribute("progressBarWidths", progressBarWidths); // 프로그레스 바 너비

        // 상세 페이지로 이동
        return "field/Detail";
    }
}
