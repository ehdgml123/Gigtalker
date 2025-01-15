package com.zerock.controller;

import com.zerock.Entity.ProductionService;
import com.zerock.Entity.Users;
import com.zerock.service.ProductionServiceService;
import com.zerock.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
@RequestMapping(value = "Order")
public class OrderController {

    @Autowired
    private ProductionServiceService productionServiceService;
    @Autowired
    private UsersService usersService;

    @GetMapping(value = "OrderDetails")
    public String orderDetail(
            @RequestParam("id") Long productId,
            @CookieValue(value = "userId", required = false) String userId,
            HttpServletRequest request,
            Model model) {

        // 세션에서 사용자 정보 확인
        Users currentUser = (Users) request.getSession().getAttribute("user");

        if (currentUser == null && userId != null) {
            // 세션에 정보가 없을 경우, 쿠키 기반으로 사용자 정보 조회
            log.info("User not found in session. Trying to fetch from cookie.");
            currentUser = usersService.getUserById(Long.parseLong(userId));
        }

        if (currentUser == null) {
            log.warn("User not logged in. Redirecting to login page.");
            return "redirect:/login/LoginMain";
        }

        log.info("Logged-in user: {}", currentUser);

        // 모델에 사용자 정보 추가
        model.addAttribute("user", currentUser);

        // 상품 정보 가져오기
        ProductionService productionService = productionServiceService.getProductById(productId);

        if (productionService == null) {
            log.error("Product not found for ID: " + productId);
            return "error/404";
        }

        model.addAttribute("productionService", productionService);

        log.info("OrderDetails page for product ID: " + productId);

        return "Order/OrderDetails";
    }
}
