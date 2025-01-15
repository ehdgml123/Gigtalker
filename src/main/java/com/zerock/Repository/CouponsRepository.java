package com.zerock.Repository;

import com.zerock.Entity.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponsRepository extends JpaRepository<Coupons, Long> {

     @Query("SELECT c FROM Coupons c WHERE c.code = :code")
     Optional<Coupons> findByCode(@Param("code") Long code);

}
