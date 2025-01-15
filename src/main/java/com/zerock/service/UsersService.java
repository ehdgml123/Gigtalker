package com.zerock.service;

import com.zerock.Entity.Users;
import com.zerock.Repository.UsersRepository;
import com.zerock.dto.MailDTO;
import com.zerock.dto.UserFormDto;
import com.zerock.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final MailService mailService; // MailService 주입
    private final ModelMapper modelMapper;

    // SHA-256을 사용한 비밀번호 해싱
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 지원하지 않습니다.", e);
        }
    }

    // 랜덤 비밀번호 생성
    private String generateRandomPassword() {
        char[] charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int idx = (int) (Math.random() * charSet.length);
            newPassword.append(charSet[idx]);
        }
        return newPassword.toString();
    }

    @Transactional
    public void sendTemporaryPassword(String email) {
        // 이메일 존재 여부 확인
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다."));

        // 임시 비밀번호 생성
        String tmpPassword = generateRandomPassword();

        // 사용자 비밀번호 업데이트 (암호화)
        user.setPassword(hashPassword(tmpPassword));
        usersRepository.save(user);

        // 이메일 생성 및 발송
        MailDTO mailDTO = mailService.createMail(tmpPassword, email);
        mailService.sendMail(mailDTO);
    }

    @Transactional
    public Users registerUser(UserFormDto userFormDto) {
        // 이메일 중복 확인
        if (usersRepository.findByEmail(userFormDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("존재하는 이메일 입니다.");
        }

        // UserFormDto를 Users 엔티티로 매핑
        Users user = modelMapper.map(userFormDto, Users.class);

        // 비밀번호 암호화 후 저장
        user.setPassword(hashPassword(userFormDto.getPassword()));

        return usersRepository.save(user);
    }

    public Users authenticateUser(String email, String password) throws Exception {
        // 사용자 검색
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("등록되지 않은 이메일입니다."));

        // 암호화된 비밀번호와 비교
        if (!user.getPassword().equals(hashPassword(password))) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public Users getUserById(Long userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    @Transactional
    public Users processOAuth2User(UserFormDto userFormDto) {
        // 이메일로 사용자 검색
        Users existingUser = usersRepository.findByEmail(userFormDto.getEmail()).orElse(null);

        if (existingUser != null) {
            // 기존 사용자 업데이트 (필요한 경우)
            existingUser.setProvider(userFormDto.getProvider());
            existingUser.setProviderId(userFormDto.getProviderId());
            existingUser.setName(userFormDto.getName());

            // PHONE과 ROLE이 없으면 기본값 유지
            if (existingUser.getPhone() == null) {
                existingUser.setPhone("");
            }
            if (existingUser.getRole() == null) {
                existingUser.setRole("");
            }

            return usersRepository.save(existingUser);
        }

        // 신규 사용자 생성
        Users newUser = modelMapper.map(userFormDto, Users.class);
        newUser.setProvider(userFormDto.getProvider());
        newUser.setProviderId(userFormDto.getProviderId());
        newUser.setName(userFormDto.getName());

        // 기본 비밀번호 설정 (OAuth2 사용자용)
        String defaultPassword = generateRandomPassword();
        newUser.setPassword(hashPassword(defaultPassword));

        // PHONE과 ROLE 기본값 설정
        newUser.setPhone("");
        newUser.setRole("");

        return usersRepository.save(newUser);
    }

    @Transactional
    public void updateUser(Users user) {
        // 사용자 정보 업데이트
        usersRepository.save(user);
    }



    @Transactional
    public boolean emailExist(String email) {
        return usersRepository.existsByEmail(email); // UsersRepository에 existsByEmail 메서드가 있어야 함
    }



    @Transactional
    public boolean updatePasswordToken(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다."));

        // 토큰 발급 제한 시간 확인 (1시간 제한)
        if (user.getTokenExpiration() != null && user.getTokenExpiration().isAfter(LocalDateTime.now())) {
            return false; // 1시간 이내에 다시 요청하면 false 반환
        }

        // 새로운 토큰 생성 및 저장
        String newToken = UUID.randomUUID().toString(); // 랜덤 토큰 생성
        user.setPasswordResetToken(newToken);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1)); // 토큰 유효 시간 설정 (1시간)

        usersRepository.save(user); // 변경된 사용자 정보 저장
        return true; // 토큰이 성공적으로 업데이트됨
    }


    public String getTmpPassword() {
        char[] charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder tmpPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) { // 10자리 길이의 비밀번호 생성
            int idx = (int) (Math.random() * charSet.length);
            tmpPassword.append(charSet[idx]);
        }
        return tmpPassword.toString();
    }


    @Transactional
    public void updatePassword(String newPassword, String email) {
        // 이메일로 사용자 찾기
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다."));

        // 비밀번호 해싱 후 저장
        String hashedPassword = hashPassword(newPassword);
        user.setPassword(hashedPassword);
        usersRepository.save(user); // 변경된 사용자 정보 저장
    }

    @Transactional
    public void updateUserInformation(Users updatedUser) {
        if (updatedUser.getId() == null) {
            throw new IllegalArgumentException("사용자 ID가 제공되지 않았습니다.");
        }

        Users existingUser = usersRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        existingUser.setName(updatedUser.getName());
        existingUser.setPhone(updatedUser.getPhone());

        usersRepository.save(existingUser);
    }

}
