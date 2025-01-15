package com.zerock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerock.Entity.Users;
import com.zerock.dto.UserFormDto;
import com.zerock.service.UsersService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Log4j2
@Controller
public class GoogleLoginController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${google.token.url}")
    private String tokenUrl;

    @Value("${google.userinfo.url}")
    private String userInfoUrl;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UsersService usersService;

    public GoogleLoginController(UsersService userService) {
        this.usersService = userService;
    }

    @GetMapping("/login/oauth2/code/google")
    public String googleCallback(@RequestParam("code") String code, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // Google OAuth2 인증 처리
            String accessToken = fetchAccessToken(code);
            Map<String, Object> userInfo = fetchUserInfo(accessToken);

            // 사용자 정보를 UserFormDto로 변환
            UserFormDto userFormDto = UserFormDto.builder()
                    .email((String) userInfo.get("email"))
                    .name((String) userInfo.get("name"))
                    .provider("google")
                    .providerId((String) userInfo.get("sub"))
                    .build();

            // 사용자 저장 또는 업데이트
            Users user = usersService.processOAuth2User(userFormDto);

            // 세션에 사용자 저장
            session.setAttribute("user", user);
            log.info("Google 로그인 성공: 세션에 저장된 사용자 정보 = {}", user);

            // 추가 정보 입력 필요 확인
            if ((user.getPhone() == null || user.getPhone().isEmpty()) ||
                    (user.getRole() == null || user.getRole().isEmpty())) {
                return "redirect:/phone-role-input";
            }

            return "redirect:/"; // 성공적으로 로그인한 경우
        } catch (Exception e) {
            log.error("Google OAuth2 인증 중 오류 발생: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Google 인증 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/login?error=true";
        }
    }

    @GetMapping(value = "/phone-role-input")
    public String showPhoneAndRoleInputForm() {
        return "oauth2/phone-role-input"; // 입력 폼 페이지 반환
    }

    @PostMapping("/submit-phone-role")
    public String processPhoneRoleInput(@RequestParam String phone, @RequestParam String role, HttpSession session, RedirectAttributes redirectAttributes) {
        Users tempUser = (Users) session.getAttribute("tempUser");

        if (tempUser == null) {
            log.warn("세션이 만료되어 사용자 정보를 찾을 수 없습니다.");
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        try {
            tempUser.setPhone(phone);
            tempUser.setRole(role);
            usersService.updateUser(tempUser);

            session.removeAttribute("tempUser");
            session.setAttribute("user", tempUser); // 사용자 세션 업데이트

            return "redirect:/";
        } catch (Exception e) {
            log.error("사용자 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 저장하는 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/phone-role-input?error=true";
        }
    }


    private String fetchAccessToken(String code) throws Exception {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "code=" + code +
                                    "&client_id=" + clientId +
                                    "&client_secret=" + clientSecret +
                                    "&redirect_uri=" + redirectUri +
                                    "&grant_type=authorization_code"
                    ))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);

            return (String) responseBody.get("access_token");
        } catch (Exception e) {
            log.error("Access Token 요청 중 오류 발생", e);
            throw new RuntimeException("Access Token 요청 실패", e);
        }
    }

    private Map<String, Object> fetchUserInfo(String accessToken) throws Exception {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(userInfoUrl + "?access_token=" + accessToken))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), Map.class);
        } catch (Exception e) {
            log.error("UserInfo 요청 중 오류 발생", e);
            throw new RuntimeException("UserInfo 요청 실패", e);
        }
    }

}