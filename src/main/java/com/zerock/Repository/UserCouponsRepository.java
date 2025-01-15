package com.zerock.Repository;

import com.zerock.Entity.UserCoupons;
import com.zerock.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponsRepository extends JpaRepository<UserCoupons, Long> {

    // 특정 사용자 ID로 쿠폰 목록 조회
    List<UserCoupons> findByUserId(Long userId);

    // 특정 사용자 에게 쿠폰이 발급 되었는지 확인
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);


    Long user(Users user);
}
