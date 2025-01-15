package com.zerock.service;

import com.zerock.Entity.MailEntity;
import com.zerock.Repository.MailRepository;
import com.zerock.dto.MailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final MailRepository mailRepository;

    private static final String title = "임시 비밀번호 안내 이메일입니다.";
    private static final String message = "안녕하세요. 임시 비밀번호 안내 메일입니다. "
            + "\n" + "회원님의 임시 비밀번호는 아래와 같습니다. 로그인 후 반드시 비밀번호를 변경해주세요." + "\n";

    @Value("${spring.mail.username}")
    private String from;

    public MailDTO createMail(String tmpPassword, String to) {
        return new MailDTO(from, to, title, message + tmpPassword);
    }

    public void sendMail(MailDTO mailDTO) {
        // 이메일 전송
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailDTO.getTo());
        mailMessage.setSubject(mailDTO.getTitle());
        mailMessage.setText(mailDTO.getMessage());
        mailMessage.setFrom(mailDTO.getFrom());
        mailMessage.setReplyTo(mailDTO.getFrom());
        mailSender.send(mailMessage);

        // 이메일 기록 저장
        saveEmailLog(mailDTO);
    }

    private void saveEmailLog(MailDTO mailDTO) {
        MailEntity mailEntity = new MailEntity();
        mailEntity.setFrom(mailDTO.getFrom());
        mailEntity.setTo(mailDTO.getTo());
        mailEntity.setTitle(mailDTO.getTitle());
        mailEntity.setMessage(mailDTO.getMessage());
        mailEntity.setSentAt(LocalDateTime.now());
        mailRepository.save(mailEntity); // 데이터베이스에 저장
    }

}
