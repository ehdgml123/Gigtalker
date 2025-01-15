package com.zerock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MailDTO {

    private String from;    // 발신자 이메일

    private String to;      // 수신자 이메일

    private String title;   // 이메일 제목

    private String message; // 이메일 내용


}
