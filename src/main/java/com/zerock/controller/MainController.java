package com.zerock.controller;

import com.zerock.Entity.Users;
import com.zerock.service.UsersService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
public class MainController {

    @Autowired
    private  UsersService usersService;

    @GetMapping(value = "/")
    public String main(Model model, HttpSession session) {

        Users loggedInUser = (Users) session.getAttribute("user");

        if (loggedInUser != null) {
            model.addAttribute("isLoggedIn", true);  // 로그인 여부
            model.addAttribute("userName", loggedInUser.getName());  // 사용자 이름
            log.info("Logged in user: {}", loggedInUser.getName());
        } else {
            model.addAttribute("isLoggedIn", false);  // 비로그인 상태
            model.addAttribute("userName", "Guest");  // 기본 사용자 이름
            log.info("No user found in session.");
        }

        return "Main";  // 메인 페이지 템플릿 반환
    }

}
