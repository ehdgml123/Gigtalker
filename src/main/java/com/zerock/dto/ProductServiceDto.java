package com.zerock.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "mainImagePathFile") // 파일 데이터 제외
@AllArgsConstructor
@NoArgsConstructor
public class ProductServiceDto {

    private Long id;

    private String title;

    private String category;

    private String subCategory;

    private String description;

    private Integer basePrice;

    private Integer maxWorkingDays;

    private Integer freeRevisions;

    private String communicationMethod;

    private MultipartFile[] productImgFileList;

    private List<ProductImgDto> productImgDtoList = new ArrayList<>();

    private MultipartFile mainImagePathFile; // 클라이언트에서 업로드된 파일
    private String mainImagePath; // 서버에 저장된 파일 경로
    private Long userId; // 사용자 ID


}
