package com.zerock.controller;

import com.zerock.dto.MailDTO;
import com.zerock.service.MailService;
import com.zerock.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final UsersService usersService;

    @PostMapping("/check/email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        if(!usersService.emailExist(email)) {
            return new ResponseEntity<>("일치하는 메일이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("이메일을 사용하는 유저가 존재합니다.", HttpStatus.OK);
    }

    @PostMapping("/send/password")
    public ResponseEntity<?> sendPassword(@RequestParam("email") String email) {
        if(!usersService.updatePasswordToken(email)) {
            return new ResponseEntity<>("비밀번호 찾기는 1시간에 한 번 가능합니다.", HttpStatus.BAD_REQUEST);
        }

        String tmpPassword = usersService.getTmpPassword();
        usersService.updatePassword(tmpPassword, email);
        MailDTO mail = mailService.createMail(tmpPassword, email);

        mailService.sendMail(mail);

        return new ResponseEntity<>("비밀번호 발급이 완료되었습니다.", HttpStatus.OK);
    }
}
