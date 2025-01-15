package com.zerock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerock.Entity.Users;
import com.zerock.dto.UserFormDto;
import com.zerock.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Log4j2
@Controller
public class KakaoLoginController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.userinfo-url}")
    private String userInfoUrl;

    @Autowired
    private UsersService usersService;

    @GetMapping("/user/login/oauth2/code/kakao")
    public String kakaoCallback(
            @RequestParam("code") String code,
            HttpSession session,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        try {
            // 카카오 인증 처리 및 사용자 저장
            String accessToken = fetchAccessToken(code);
            Map<String, Object> userInfo = fetchUserInfo(accessToken);

            String email = (String) ((Map<String, Object>) userInfo.get("kakao_account")).get("email");
            String nickname = (String) ((Map<String, Object>) userInfo.get("properties")).get("nickname");
            String kakaoId = String.valueOf(userInfo.get("id"));

            UserFormDto userFormDto = UserFormDto.builder()
                    .email(email)
                    .name(nickname)
                    .provider("kakao")
                    .providerId(kakaoId)
                    .build();

            Users user = usersService.processOAuth2User(userFormDto);

            // 세션에 사용자 저장
            session.setAttribute("user", user);
            log.info("카카오 로그인 성공: 세션에 저장된 사용자 정보 = {}", user);

            // 쿠키에 userId 저장
            Cookie userIdCookie = new Cookie("userId", String.valueOf(user.getId()));
            userIdCookie.setPath("/");
            userIdCookie.setHttpOnly(true);
            userIdCookie.setMaxAge(60 * 60 * 24); // 1일
            response.addCookie(userIdCookie);

            if ((user.getPhone() == null || user.getPhone().isEmpty()) ||
                    (user.getRole() == null || user.getRole().isEmpty())) {
                session.setAttribute("tempUser", user);
                return "redirect:/phone-role-input";
            }

            return "redirect:/";
        } catch (Exception e) {
            log.error("카카오 OAuth2 인증 중 오류 발생: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "카카오 인증 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/login?error=true";
        }
    }

    private String fetchAccessToken(String code) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code" +
                                "&client_id=" + clientId +
                                "&redirect_uri=" + redirectUri +
                                "&code=" + code
                ))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // JSON 응답에서 액세스 토큰 추출
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);

        return (String) responseBody.get("access_token");
    }

    private Map<String, Object> fetchUserInfo(String accessToken) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userInfoUrl))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // JSON 응답에서 사용자 정보 추출
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), Map.class);
    }
}
