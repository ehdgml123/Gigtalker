package com.zerock.dto;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFormDto {


    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "핸드폰 번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "유효한 핸드폰 번호를 입력하세요.")
    private String phone;

    @NotBlank(message = "틱톡커에 오신 이유를 선택해주세요!")
    private String role; // 단일 역할로 변경

    private String provider;     // OAuth 제공자 (예: google, facebook)

    private String providerId;  // OAuth 제공자가 제공하는 고유 ID

    private String phoneVerificationCode; // 핸드폰 인증 번호
}
