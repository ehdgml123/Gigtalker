package com.zerock.controller;

import com.zerock.Entity.Users;
import com.zerock.dto.LoginFormDto;
import com.zerock.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/login")
@Log4j2
public class LoginMainController {

    private final UsersService usersService;

    public LoginMainController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping(value = "/loginMain")
    public String loginMain(Model model) {
        return "login/loginMain";
    }

    @GetMapping(value = "/EmailLogin.html")
    public String EmailLogin(Model model) {
        model.addAttribute("loginFormDto", new LoginFormDto());
        return "login/emailLogin";
    }

    @PostMapping(value = "/EmailLogin.html")
    public String EmailLoginPost(
            @Valid @ModelAttribute("loginFormDto") LoginFormDto loginFormDto,
            BindingResult bindingResult,
            HttpSession session,
            HttpServletResponse response, // HttpServletResponse 추가
            Model model) {

        // 입력값 유효성 검증
        if (bindingResult.hasErrors()) {
            log.warn("로그인 폼 유효성 검사 실패: {}", bindingResult.getAllErrors());
            return "login/emailLogin";
        }

        try {
            // 사용자 인증
            Users users = usersService.authenticateUser(loginFormDto.getEmail(), loginFormDto.getPassword());

            // 세션에 사용자 정보 저장
            session.setAttribute("user", users);
            session.setAttribute("isLoggedIn", true); // 로그인 상태 설정
            session.setAttribute("userId", users.getId().toString()); // 사용자 ID 저장
            session.setAttribute("userRoles", List.of(users.getRole())); // 단일 역할을 리스트로 저장

            // 쿠키 생성 및 설정
            Cookie userIdCookie = new Cookie("userId", String.valueOf(users.getId()));
            userIdCookie.setPath("/"); // 애플리케이션 전역에서 접근 가능
            userIdCookie.setHttpOnly(true); // JavaScript로 접근 불가능 (보안 강화)
            userIdCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효 기간: 1일
            response.addCookie(userIdCookie); // 응답에 쿠키 추가

            model.addAttribute("sessionMessage", "로그인에 성공했습니다.");
            log.info("로그인 성공: 사용자 ID = {}", users.getId());

            // 로그인 성공 시 메인 페이지로 리다이렉션
            return "redirect:/";
        } catch (Exception e) {
            // 인증 실패 처리
            log.error("로그인 실패: {}", e.getMessage());
            bindingResult.rejectValue("email", "error", e.getMessage());
            return "login/emailLogin";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/"; // 로그아웃 후 메인 페이지로 리다이렉션
    }



    //passwordfind page
    @GetMapping(value = "passwordfind")
    public String passwordfind(Model model) {

        return "login/passwordfind";
    }
}
