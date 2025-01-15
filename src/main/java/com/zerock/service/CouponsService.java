package com.zerock.service;

import com.zerock.Entity.Coupons;
import com.zerock.Entity.UserCoupons;
import com.zerock.Entity.Users;
import com.zerock.Repository.CouponsRepository;
import com.zerock.Repository.UserCouponsRepository;
import com.zerock.dto.CouponsDto;
import com.zerock.dto.UserCouponsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponsService {

    @Autowired
    private UserCouponsRepository userCouponsRepository;

    @Autowired
    private CouponsRepository couponsRepository;

    @Transactional
    public boolean applyCouponToUser(String couponCode, Long userId) {
        couponCode = couponCode.trim();
        log.info("입력된 쿠폰 코드: [{}], 길이: {}", couponCode, couponCode.length());

        // 쿠폰 코드 Long 타입 변환
        Optional<Coupons> optionalCoupon = couponsRepository.findByCode(Long.parseLong(couponCode)); // `Long` 사용
        if (optionalCoupon.isEmpty()) {
            log.warn("조회된 쿠폰 코드가 없습니다.");
            throw new IllegalArgumentException("유효하지 않은 쿠폰 코드입니다.");
        }

        Coupons coupon = optionalCoupon.get();

        // 쿠폰 만료 체크
        if (coupon.getCouponExpirationDate().isBefore(LocalDateTime.now().toLocalDate())) {
            log.warn("만료된 쿠폰입니다: {}", couponCode);
            throw new IllegalArgumentException("만료된 쿠폰입니다.");
        }

        // 이미 발급된 쿠폰인지 확인
        boolean alreadyIssued = userCouponsRepository.existsByUserIdAndCouponId(userId, coupon.getId());
        if (alreadyIssued) {
            log.warn("사용자 {}에게 이미 발급된 쿠폰입니다: {}", userId, couponCode);
            return false;
        }

        // UserCoupons 객체 생성 및 저장
        Users user = new Users();
        user.setId(userId);

        UserCoupons userCoupon = new UserCoupons();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setStatus("사용가능");
        userCoupon.setIssuedAt(LocalDateTime.now()); // 현재 시간 설정

        userCouponsRepository.save(userCoupon);
        log.info("사용자 {}에게 쿠폰 '{}'가 성공적으로 발급되었습니다.", userId, couponCode);
        return true;
    }

    // 사용자 쿠폰 조회 메서드
    @Transactional
    public List<UserCouponsDto> getUserCoupons(Long userId) {
        // 데이터베이스에서 사용자 쿠폰 조회
        List<UserCoupons> userCouponsList = userCouponsRepository.findByUserId(userId);

        // DateTimeFormatter 선언
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // DTO로 변환
        return userCouponsList.stream().map(userCoupon -> new UserCouponsDto(
                userCoupon.getId(),
                userCoupon.getUser().getId(),
                new CouponsDto(
                        userCoupon.getCoupon().getId(),
                        userCoupon.getCoupon().getCode(),
                        userCoupon.getCoupon().getName(),
                        userCoupon.getCoupon().getDiscountAmount(),
                        userCoupon.getCoupon().getMinOrderAmount(),
                        userCoupon.getCoupon().getCouponExpirationDate(),
                        userCoupon.getCoupon().getType()
                ),
                userCoupon.getStatus(),
                userCoupon.getIssuedAt() != null ? userCoupon.getIssuedAt().format(dateFormatter) : null // 날짜만 포맷
        )).collect(Collectors.toList());
    }
}
