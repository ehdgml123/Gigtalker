package com.zerock.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.zerock.Repository.UserCouponsRepository;import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 별점

    @Column(length = 1000)
    private String review; // 리뷰 내용

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false) // 결제와 1:1 연결
    @JsonBackReference // Payment와의 관계에서 순환 참조 방지
    private Payment payment; // 어떤 결제에 대한 리뷰인지
}