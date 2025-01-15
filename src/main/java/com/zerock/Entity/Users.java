package com.zerock.Entity;


import com.zerock.dto.UserFormDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@ToString
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = true)
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionService> productionServices;

    private String provider;     // OAuth 제공자 (예: google, facebook)

    private String providerId;  // OAuth 제공자가 제공하는 고유 ID


    // 비밀번호 재설정 토큰
    private String passwordResetToken;

    // 토큰 유효기간
    private LocalDateTime tokenExpiration;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupons> userCoupons;



}
