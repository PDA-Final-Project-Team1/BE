package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserAdditionalInfo;
import com.team1.etuser.user.dto.LoginReq;
import com.team1.etuser.user.dto.SignUpReq;
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

    public void signup(SignUpReq requestDto) {
        if (userRepository.existsByUid(requestDto.getUid())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
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
                .deposit(BigDecimal.valueOf(5000000))
                .point(50000)
                .account(accountNumber)
                .build();
        userAdditionalInfoRepository.save(userAdditionalInfo);
    }

    public String login(LoginReq requestDto) {
        User user = userRepository.findByUid(requestDto.getUid())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 아이디입니다."));
        if (!passwordEncoder.matches(requestDto.getPwd(), user.getPwd())) {
            throw new RuntimeException("아이디와 비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenProvider.createToken(user.getId());
    }

    public String generateAccount() {
        SecureRandom random = new SecureRandom();
        int part1 = 1000 + random.nextInt(9000);
        int part2 = 1000 + random.nextInt(9000);
        int part3 = 1000 + random.nextInt(9000);
        return String.format("%04d-%04d-%04d", part1, part2, part3);
    }
}
