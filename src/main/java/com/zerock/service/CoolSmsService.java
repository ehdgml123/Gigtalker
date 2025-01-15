package com.zerock.service;

import groovy.util.logging.Log4j2;

import net.nurigo.java_sdk.api.Message;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Random;

@Service
@Log4j2
public class CoolSmsService {

   @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    private static final String REDIS_SMS_PREFIX = "SMS_";


    public void sendSmsWithCode(String to) {
        try {
            // 4자리 인증번호 생성
            String verificationCode = generateRandomNumber();

            // Coolsms API 호출 준비
            Message coolsms = new Message(apiKey, apiSecret);

            HashMap<String, String> params = new HashMap<>();
            params.put("to", to);
            params.put("from", fromPhoneNumber);
            params.put("type", "sms");
            params.put("text", "인증번호는 [" + verificationCode + "] 입니다.");

            // API 호출
            JSONObject response = coolsms.send(params);
            System.out.println("Coolsms API Response: " + response.toString());

            // 성공 여부 확인 (Long으로 캐스팅 후 int 변환)
            long successCountLong = (long) response.get("success_count");
            int successCount = Math.toIntExact(successCountLong);

            if (successCount > 0) {
                // 성공 처리: Redis에 인증번호 저장
                redisTemplate.opsForValue().set(REDIS_SMS_PREFIX + to, verificationCode, Duration.ofMinutes(5));
            } else {
                // 실패 처리
                String errorMessage = response.get("result_message") != null
                        ? response.get("result_message").toString()
                        : "Unknown error";
                System.err.println("Coolsms Error: " + errorMessage);
                throw new RuntimeException("Failed to send SMS: " + errorMessage);
            }
        } catch (Exception e) {
            System.err.println("Coolsms Exception: " + e.getMessage());
            throw new RuntimeException("Failed to send SMS", e);
        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String storedCode = redisTemplate.opsForValue().get(REDIS_SMS_PREFIX + phoneNumber);
        return storedCode != null && storedCode.equals(code);
    }

    private String generateRandomNumber() {
        return String.format("%04d", new Random().nextInt(10000)); // 4자리 숫자 생성
    }
}
