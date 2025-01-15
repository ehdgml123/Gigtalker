package com.zerock.controller;

import com.zerock.Entity.ProductionService;
import com.zerock.Entity.Users;
import com.zerock.service.ProductionServiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class fieldController {

    private final ProductionServiceService productionServiceService;

    public fieldController(ProductionServiceService productionServiceService) {
        this.productionServiceService = productionServiceService;
    }

    @GetMapping(value = "/video")
    public String fieldVideo(Model model, HttpServletRequest request) {
        String category = "영상/사진"; // 조회하려는 카테고리 이름

        Users currentUser = (Users) request.getSession().getAttribute("user");


        model.addAttribute("userName", currentUser.getName());
        // 이미지를 포함한 상품 데이터 가져오기
        List<ProductionService> productList = productionServiceService.getProductsWithImagesByCategory(category);
        model.addAttribute("productList", productList);

        // 로그 추가
        System.out.println("Fetched productList size: " + productList.size());
        productList.forEach(product -> {
            System.out.println("Product ID: " + product.getId());
            System.out.println("Product Base Price: " + product.getBasePrice());
            if (product.getProductionServiceImgs() != null) {
                product.getProductionServiceImgs().forEach(img -> {
                    System.out.println("Image URL: " + img.getImgUrl());
                });
            }
        });

        return "field/video"; // Thymeleaf 템플릿 경로
    }

}
