package com.zerock.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@ToString
@Getter
@Setter
@Table(name = "ProductionService")
@AllArgsConstructor
@NoArgsConstructor
public class ProductionService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 제목

    private String category; // 카테고리

    private String subCategory;

    private String description; // 서비스 설명s

    private Integer basePrice; // 기본 금액

    private Integer maxWorkingDays; // 최대 작업 기간

    private Integer freeRevisions; // 무료 수정 횟수

    private String communicationMethod; // 대화 방법 (예: 채팅, 이메일 등)


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @OneToMany(mappedBy = "productionService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionServiceImg> productionServiceImgs;


    @OneToMany(mappedBy = "productionService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments; // 상품에 대한 결제 리스트

}
