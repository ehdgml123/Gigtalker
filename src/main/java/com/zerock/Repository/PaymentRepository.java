package com.zerock.Repository;

import com.zerock.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    //orderNumber를 기준으로 Payment 엔티티를 검색
    Optional<Payment> findByOrderNumber(String orderNumber);

    // impUid(아임포트 결제 고유 ID)를 기준으로 Payment 엔티티를 검색
    Optional<Payment> findByImpUid(String impUid);


    // 사용자 ID 기반 조회
    List<Payment> findAllByUserId(Long userId);
}
