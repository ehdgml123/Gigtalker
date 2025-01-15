package com.zerock.controller;

import com.zerock.Entity.Payment;
import com.zerock.Entity.Users;
import com.zerock.dto.UserCouponsDto;
import com.zerock.service.CouponsService;
import com.zerock.service.PaymentService;
import com.zerock.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;


@Controller
@Log4j2
public class UserController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CouponsService couponsService;

    @GetMapping(value = "/EditInformation")
    public String editInformation(Model model, HttpSession session) {
        // 세션에서 사용자 객체 가져오기
        Users loggedInUser = (Users) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 리다이렉트
        }

        // 사용자 정보를 데이터베이스에서 가져오기
        Users userFromDb = usersService.getUserById(loggedInUser.getId());

        if (userFromDb == null) {
            return "redirect:/login"; // 유효하지 않은 사용자
        }

        // 모델에 사용자 정보 추가
        model.addAttribute("user", userFromDb);
        return "member/editInformation"; // Thymeleaf 템플릿 렌더링
    }


    @PostMapping("/EditInformation")
    public String editInformation(@ModelAttribute Users user,
                                  @RequestParam(required = false) String newPassword,
                                  @RequestParam(required = false) String confirmPassword,
                                  Model model) {
        if (user.getId() == null || user.getEmail() == null) {
            model.addAttribute("errorMessage", "유효하지 않은 사용자 정보입니다.");
            return "member/editInformation";
        }

        try {
            if (newPassword != null && !newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    model.addAttribute("errorMessage", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
                    return "member/editInformation";
                }
                usersService.updatePassword(newPassword, user.getEmail());
            }

            usersService.updateUserInformation(user);
            model.addAttribute("successMessage", "변경사항이 성공적으로 저장되었습니다.");
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/editInformation";
        }
    }



    @GetMapping(value = "/UserMyPage")
    public String myPage(Model model, HttpSession session) {

        Users loggedInUser = (Users) session.getAttribute("user");

        if(loggedInUser != null) {
            model.addAttribute("userName", loggedInUser.getName());
        } else {
             model.addAttribute("userName", "Guest");
        }

        return "member/UserMyPage";
    }

    @GetMapping("/order-management")
    public String orderManagement(HttpServletRequest request, Model model) {
        // 세션에서 현재 사용자 정보 가져오기
        Users currentUser = (Users) request.getSession().getAttribute("user");

        if (currentUser == null) {
            log.warn("User not logged in. Redirecting to login page.");
            return "redirect:/login/LoginMain";
        }

        // 사용자 ID로 결제 내역 가져오기
        List<Payment> payments = paymentService.getPaymentsByUserId(currentUser.getId());

        // 모델에 결제 내역 추가
        model.addAttribute("payments", payments);
        model.addAttribute("userName", currentUser.getName());

        log.info("결제 내역 조회: 사용자 ID = {}", currentUser.getId());
        return "member/orderManagement";
    }

    @GetMapping(value = "/my-review")
    public String myReview(Model model) {

        return "member/my-review";
    }

    @GetMapping(value = "/pay-history")
    public String payHistory(HttpServletRequest request,Model model) {

        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login/LoginMain"; // 로그인 페이지로 리디렉션
        }

        // 사용자 이름을 Model에 추가
        model.addAttribute("userName", currentUser.getName()); // 사용자 이름 전달


        // 사용자 ID로 결제 내역 조회
        List<Payment> paymentList = paymentService.getPaymentsByUserId(currentUser.getId());
        model.addAttribute("paymentList", paymentList);

        return "member/payHistory";
    }



    /* 받은 쿠폰함 page*/
    @GetMapping(value = "mycoupon")
    public String myCoupon(Model model, HttpSession session) {

        Users loggedInUser = (Users) session.getAttribute("user");

        if(loggedInUser == null) {
            return "redirect:/login/LoginMain";
        }

        // 사용자 ID 로 쿠폰 데이터를 가져옴
        List<UserCouponsDto> userCoupons = couponsService.getUserCoupons(loggedInUser.getId());

        //쿠폰 데이터를 모델에 추가
        model.addAttribute("userCoupons", userCoupons);
        model.addAttribute("userName", loggedInUser.getName());

        return "member/mycoupon";
    }


    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam String couponCode, HttpSession session, Model model) {

        log.info("입력된 쿠폰 코드:{}", couponCode);

        Users loggedInUser = (Users) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/login"; // 로그인되지 않은 경우
        }

        Long userId = loggedInUser.getId();

        boolean success = couponsService.applyCouponToUser(couponCode, userId);

        if (success) {
            model.addAttribute("successMessage", "쿠폰이 성공적으로 발급되었습니다.");
        } else {
            model.addAttribute("errorMessage", "이미 발급된 쿠폰이거나 유효하지 않은 쿠폰 코드입니다.");
        }

        List<UserCouponsDto> userCoupons = couponsService.getUserCoupons(userId);
        model.addAttribute("userCoupons", userCoupons);

        return "member/mycoupon";
    }

}
