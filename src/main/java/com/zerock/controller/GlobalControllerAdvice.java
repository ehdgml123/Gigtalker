package com.zerock.controller;

import com.zerock.Entity.Users;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
@Log4j2
public class GlobalControllerAdvice {


    @ModelAttribute
    public void addAttributes(HttpSession session, Model model) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        List<String> userRoles = (List<String>) session.getAttribute("userRoles");
        Users currentUser = (Users) session.getAttribute("user");

        model.addAttribute("isLoggedIn", isLoggedIn != null && isLoggedIn);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("currentUser", currentUser);
    }
}
