package com.zerock.controller;

import com.zerock.Entity.ProductionService;
import com.zerock.Entity.Users;
import com.zerock.Repository.UsersRepository;
import com.zerock.dto.ProductFormDto;
import com.zerock.dto.ProductServiceDto;
import com.zerock.service.ProductionServiceService;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Controller
@RequestMapping(value = "/expert")
public class expertMainController {


    @Autowired
    private ProductionServiceService productionServiceService;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping(value = "/main")
    public String expertMain(Model model,HttpServletRequest request) {

        Users currentUser = (Users) request.getSession().getAttribute("user");

        model.addAttribute("userName", currentUser.getName());

        return "expert/expertMain";
    }

    @GetMapping(value = "/productnew")
    public String productNew(Model model) {
        return "expert/productNew";

    }


    @GetMapping(value = "/ExpertCenter")
    public String expertCenter(Model model, HttpServletRequest request) {


        Users currentUser = (Users) request.getSession().getAttribute("user");

        model.addAttribute("userName", currentUser.getName());

        return "expert/ExpertCenter";
    }

    @GetMapping(value = "/ProductManagement")
    public String productManagement(Model model,HttpServletRequest request) {

        Users currentUser = (Users) request.getSession().getAttribute("user");



        List<ProductionService> productList = productionServiceService.getAllProducts();

        // 상품 수량 계산
        int productCount = productList.size();


        model.addAttribute("userName", currentUser.getName());

        // 모델에 상품 데이터 추가
        model.addAttribute("productList", productList);
        model.addAttribute("productCount", productCount);

        return "expert/ProductManagement";
    }




    @GetMapping("/productionProduct")
    public String productionProduct(Model model,
                                    @CookieValue(value = "userId", required = false) String userId,
                                    HttpSession session) {
        log.info("쿠키에서 가져온 userId: {}", userId);

        if (userId == null) {
            userId = (String) session.getAttribute("userId");
            log.info("세션에서 가져온 userId: {}", userId);
        }

        if (userId == null) {
            log.warn("userId가 설정되지 않았습니다. 로그인 페이지로 이동합니다.");
            model.addAttribute("error", "로그인이 필요합니다.");
            return "login/emailLogin"; // 로그인 페이지로 이동
        }

        ProductServiceDto productServiceDto = new ProductServiceDto();
        model.addAttribute("productServiceDto", productServiceDto);
        return "expert/productionProduct";
    }

    @PostMapping("/productionProduct")
    public String postProductionProduct(
            @Valid @ModelAttribute("productServiceDto") ProductServiceDto productServiceDto,
            BindingResult bindingResult,
            @CookieValue(value = "userId", required = false) String userId,
            HttpSession session,
            Model model) {

        log.info("폼 데이터 수신 시작");

        if (userId == null) {
            userId = (String) session.getAttribute("userId");
        }

        if (userId == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "login/emailLogin";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "입력값에 오류가 있습니다.");
            return "expert/productionProduct";
        }

        // DTO 전체 로그 추가
        log.info("ProductServiceDto: {}", productServiceDto);

        // 파일 수신 여부 확인 및 로깅 추가
        MultipartFile[] productImgFiles = productServiceDto.getProductImgFileList();
        if (productImgFiles == null || productImgFiles.length == 0) {
            log.warn("이미지 파일이 전송되지 않았습니다.");
            model.addAttribute("error", "이미지 파일이 반드시 포함되어야 합니다.");
            return "expert/productionProduct";
        } else {
            log.info("전송된 이미지 파일 수: {}", productImgFiles.length);
            for (MultipartFile file : productImgFiles) {
                log.info("Received file: {}", file.getOriginalFilename());
            }
        }

        try {
            Users user = usersRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID"));

            // 서비스 호출
            Long savedId = productionServiceService.saveItem(productServiceDto, user, Arrays.asList(productImgFiles));

            model.addAttribute("message", "상품이 성공적으로 등록되었습니다!");
            return "redirect:/";
        } catch (Exception e) {
            log.error("상품 등록 중 오류 발생", e);
            model.addAttribute("error", "상품 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("productServiceDto", productServiceDto);
            return "expert/productionProduct";
        }
    }

}
