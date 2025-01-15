package com.zerock.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "emailRequest")
public class MailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false , name = "\"from\"")
    private String from; // 발신자 이메일

    @Column(nullable = false, name = "\"to\"")
    private String to; // 수신자 이메일

    @Column(nullable = false)
    private String title; // 이메일 제목

    @Lob // Oracle의 CLOB 데이터 타입으로 매핑
    @Column(name = "message", nullable = false)
    private String message; // 이메일 내용

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt; // 이메일 발송 시간

}
