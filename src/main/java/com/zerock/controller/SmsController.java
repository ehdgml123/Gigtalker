package com.zerock.controller;

import com.zerock.service.CoolSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private CoolSmsService coolSmsService;

    @PostMapping("/send")
    public String sendSms(@RequestParam String phoneNumber) {
        try {
            coolSmsService.sendSmsWithCode(phoneNumber);
            return "SMS 인증번호가 전송되었습니다.";
        } catch (Exception e) {
            return "SMS 전송에 실패했습니다. 오류: " + e.getMessage();
        }
    }

    @PostMapping("/verify")
    public String verifySms(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean isValid = coolSmsService.verifyCode(phoneNumber, code);
        return isValid ? "인증 성공" : "인증 실패: 잘못된 인증번호입니다.";
    }

}
