package com.zerock.controller;

import com.zerock.dto.UserFormDto;
import com.zerock.exception.EmailAlreadyExistsException;
import com.zerock.service.UsersService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/join")
public class JoinController {

    private static final Logger logger = LoggerFactory.getLogger(JoinController.class);
    private final UsersService usersService;
    private final RedisTemplate<String, String> redisTemplate;

    public JoinController(UsersService usersService, RedisTemplate<String, String> redisTemplate) {
        this.usersService = usersService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping(value = "/joinMain")
    public String joinMain(Model model) {
        return "Join/JoinMain";
    }

    @GetMapping(value = "/EmailJoin.html")
    public String EmailJoin(Model model) {
        model.addAttribute("user", new UserFormDto());
        return "Join/EmailJoin";
    }

    @PostMapping(value = "/EmailJoin.html")
    public String EmailJoinPost(@Valid @ModelAttribute("user") UserFormDto userFormDto,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "Join/EmailJoin";
        }

        // role 필드 로그 출력
        logger.info("Registering user with role: {}", userFormDto.getRole());

        if (userFormDto.getRole() == null || userFormDto.getRole().isEmpty()) {
            bindingResult.rejectValue("role", "roleRequired", "틱톡커에 오신 이유를 선택해주세요!");
            return "Join/EmailJoin";
        }

        // 핸드폰 인증 여부 확인
        String storedCode = redisTemplate.opsForValue().get("SMS_" + userFormDto.getPhone());
        if (storedCode == null || !storedCode.equals(userFormDto.getPhoneVerificationCode())) {
            bindingResult.rejectValue("phone", "phoneVerificationFailed", "핸드폰 인증이 완료되지 않았습니다.");
            return "Join/EmailJoin";
        }

        try {
            usersService.registerUser(userFormDto);
            model.addAttribute("successMessage", "회원가입이 완료되었습니다.");
            return "redirect:/";
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "emailAlreadyExists", e.getMessage());
            return "Join/EmailJoin";
        }
    }
}
