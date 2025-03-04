package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserAdditionalInfo;
import com.team1.etuser.user.dto.LoginRequestDto;
import com.team1.etuser.user.dto.SignUpRequestDto;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import com.team1.etuser.user.repository.UserRepository;
import com.team1.etuser.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAdditionalInfoRepository userAdditionalInfoRepository;

    public void signup(SignUpRequestDto requestDto) {
        if (userRepository.existsByUid(requestDto.getUid())) {
            throw new RuntimeException("User already exists");
        }
        User user = User.builder()
                .uid(requestDto.getUid())
                .pwd(passwordEncoder.encode(requestDto.getPwd()))
                .name(requestDto.getName())
                .build();
        userRepository.save(user);

        String accountNumber = generateAccount();
        UserAdditionalInfo userAdditionalInfo = UserAdditionalInfo.builder()
                .user(user)
                .deposit(BigDecimal.ZERO)
                .point(0)
                .account(accountNumber)
                .build();
        userAdditionalInfoRepository.save(userAdditionalInfo);
    }

    public String login(LoginRequestDto requestDto) {
        User user = userRepository.findByUid(requestDto.getUid())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(requestDto.getPwd(), user.getPwd())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtTokenProvider.createToken(user.getId());
    }

    public void logout(String token) {
        // JWT 블랙리스트 처리 (예: Redis에 저장)
    }

    public String generateAccount() {
        SecureRandom random = new SecureRandom();
        int part1 = 1000 + random.nextInt(9000);
        int part2 = 1000 + random.nextInt(9000);
        int part3 = 1000 + random.nextInt(9000);
        return String.format("%04d-%04d-%04d", part1, part2, part3);
    }
}