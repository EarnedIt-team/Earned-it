package _team.earnedit.service;

import _team.earnedit.dto.auth.*;
import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.entity.Term;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.jwt.JwtUtil;
import _team.earnedit.repository.SalaryRepository;
import _team.earnedit.repository.TermRepository;
import _team.earnedit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TermRepository termRepository;
    private final SalaryRepository salaryRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;


    // 회원가입 (LOCAL)
    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        String email = requestDto.getEmail();
        if (!emailVerificationService.isEmailVerified(email)) {
            throw new UserException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        String nickname = generateUniqueNickname();

        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .provider(User.Provider.LOCAL)
                        .status(User.Status.ACTIVE)
                        .isDarkMode(false)
                        .isPublic(false)
                        .build()
        );

        requestDto.getTerms().forEach(termRequestDto -> {
            Term term = Term.builder()
                    .user(user)
                    .type(termRequestDto.getType())
                    .isChecked(termRequestDto.isChecked())
                    .build();
            termRepository.save(term);
        });

        return new SignUpResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }

    // 로그인
    @Transactional
    public SignInResponseDto signIn(SignInRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND));

        if (user.getStatus() == User.Status.DELETED) {
            throw new UserException(ErrorCode.USER_ALREADY_DELETED);
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);
        }

        return generateLoginResponse(user);
    }

    // 로그인 연장 (refresh)
    @Transactional
    public RefreshResponseDto refreshAccessToken(String refreshToken) {
        String token = jwtUtil.extractBearerPrefix(refreshToken);

        if (!jwtUtil.validateRefreshToken(token)) {
            throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtUtil.getUserIdFromRefreshToken(token);

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        String savedToken = redisTemplate.opsForValue().get("refresh:" + userId);

        if (savedToken == null || !savedToken.equals(token)) {
            throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(new JwtUserInfoDto(user.getId()));
        String newRefreshToken = jwtUtil.generateRefreshToken(new JwtUserInfoDto(user.getId()));

        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                newRefreshToken,
                Duration.ofMillis(jwtUtil.getRefreshTokenExpireTime())
        );

        return new RefreshResponseDto(newAccessToken, newRefreshToken);
    }

    // JWT 발급 및 로그인 응답 생성
    private SignInResponseDto generateLoginResponse(User user) {
        String[] tokens = jwtUtil.generateToken(new JwtUserInfoDto(user.getId()));
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        redisTemplate.opsForValue()
                .set("refresh:" + user.getId(), refreshToken, Duration.ofMillis(jwtUtil.getRefreshTokenExpireTime()));

        boolean hasSalary = salaryRepository.existsByUserId(user.getId());

        return new SignInResponseDto(accessToken, refreshToken, user.getId(), hasSalary);
    }

    private String generateUniqueNickname() {
        Random random = new Random();
        String nickname;
        do {
            int randomNumber = random.nextInt(1_000_000);  // 0 ~ 999,999
            nickname = "익명의사용자" + randomNumber;
        } while (userRepository.existsByNickname(nickname));
        return nickname;
    }

}