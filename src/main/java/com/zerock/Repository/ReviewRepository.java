package com.zerock.Repository;

import com.zerock.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {


    // 특정 상품에 연결된 모든 리뷰 조회
    List<Review> findByPayment_ProductionService_Id(Long productId);

}
