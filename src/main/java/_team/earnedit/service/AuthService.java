package _team.earnedit.service;

import _team.earnedit.dto.auth.*;
import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.socialLogin.AppleSignInRequestDto;
import _team.earnedit.dto.socialLogin.AppleUserInfoDto;
import _team.earnedit.dto.socialLogin.KakaoSignInRequestDto;
import _team.earnedit.dto.socialLogin.KakaoUserInfoDto;
import _team.earnedit.entity.Term;
import _team.earnedit.entity.User;
import _team.earnedit.entity.User.Provider;
import _team.earnedit.entity.User.Status;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.jwt.JwtUtil;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.SalaryRepository;
import _team.earnedit.repository.TermRepository;
import _team.earnedit.repository.UserRepository;
import _team.earnedit.service.socialLogin.AppleOAuthService;
import _team.earnedit.service.socialLogin.KakaoOAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TermRepository termRepository;
    private final SalaryRepository salaryRepository;
    private final EmailVerificationService emailVerificationService;
    private final KakaoOAuthService kakaoOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EntityFinder entityFinder;
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
        User user = userRepository.findByEmailAndProvider(requestDto.getEmail(), Provider.LOCAL)
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (user.getStatus() == Status.DELETED) {
            recoverIfEligible(user);
        }

        return generateLoginResponse(user);
    }


    // 소셜 로그인 : KAKAO
    @Transactional
    public SignInResponseDto signInWithKakao(KakaoSignInRequestDto requestDto) {
        KakaoUserInfoDto kakaoUserInfo = kakaoOAuthService.getUserInfo(requestDto.getAccessToken());

        String kakaoId = kakaoUserInfo.getId();
        String email = kakaoUserInfo.getEmail();
        String nickname = generateUniqueNickname();
        String profileImage = kakaoUserInfo.getProfileImage();
        String safeEmail = (email != null) ? email : "kakao_" + kakaoId + "@kakao-user.com";

        return userRepository.findByProviderAndProviderId(Provider.KAKAO, kakaoId)
                .map(user -> {
                    if (user.getStatus() == Status.DELETED) {
                        user = recoverIfEligible(user);
                    }
                    return generateLoginResponse(user);
                })
                .orElseGet(() -> {
                    User user = userRepository.save(
                            User.builder()
                                    .provider(Provider.KAKAO)
                                    .providerId(kakaoId)
                                    .email(safeEmail)
                                    .nickname(nickname)
                                    .profileImage(profileImage)
                                    .status(Status.ACTIVE)
                                    .build()
                    );
                    return generateLoginResponse(user);
                });
    }



    // 소셜 로그인 : APPLE
    @Transactional
    public SignInResponseDto signInWithApple(AppleSignInRequestDto requestDto) {
        AppleUserInfoDto userInfo = appleOAuthService.getUserInfo(requestDto.getIdToken());

        String appleId = userInfo.getSub();
        String email = userInfo.getEmail();
        String nickname = generateUniqueNickname();
        String safeEmail = (email != null) ? email : "apple_" + appleId + "@apple-user.com";

        return userRepository.findByProviderAndProviderId(Provider.APPLE, appleId)
                .map(user -> {
                    if (user.getStatus() == Status.DELETED) {
                        user = recoverIfEligible(user);
                    }
                    return generateLoginResponse(user);
                })
                .orElseGet(() -> {
                    User user = userRepository.save(
                            User.builder()
                                    .provider(Provider.APPLE)
                                    .providerId(appleId)
                                    .email(safeEmail)
                                    .nickname(nickname)
                                    .status(Status.ACTIVE)
                                    .build()
                    );
                    return generateLoginResponse(user);
                });
    }



    // 로그인 연장 (refresh)
    @Transactional
    public RefreshResponseDto refreshAccessToken(String refreshToken) {
        String token = jwtUtil.extractBearerPrefix(refreshToken);

        if (!jwtUtil.validateRefreshToken(token)) {
            throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtUtil.getUserIdFromRefreshToken(token);

        User user = entityFinder.getUserOrThrow(Long.valueOf(userId));

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



    // 로그아웃
    @Transactional
    public void signOut(Long userId, String accessToken) {
        // Redis에서 refresh token 삭제
        redisTemplate.delete("refresh:" + userId);

        // access token 블랙리스트 등록
        Date expiration = jwtUtil.getAccessTokenExpiration(accessToken);
        long now = System.currentTimeMillis();
        long ttl = expiration.getTime() - now;

        redisTemplate.opsForValue()
                .set("BL:" + accessToken, "logout", ttl, TimeUnit.MILLISECONDS);
    }




    /*
     *  Util 메서드 ~~~~~~~~~~~~~~
     */


    // JWT 발급 및 로그인 응답 생성
    private SignInResponseDto generateLoginResponse(User user) {
        String[] tokens = jwtUtil.generateToken(new JwtUserInfoDto(user.getId()));
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        redisTemplate.opsForValue()
                .set("refresh:" + user.getId(), refreshToken, Duration.ofMillis(jwtUtil.getRefreshTokenExpireTime()));

        boolean hasAgreedTerm = termRepository.existsByUserId(user.getId());

        return new SignInResponseDto(accessToken, refreshToken, user.getId(), hasAgreedTerm);
    }

    // 탈퇴 유저 복구
    private User recoverIfEligible(User user) {
        if (user.getStatus() != Status.DELETED) return user;
        // status==Deleted 인데, deletedAt 이 null 이거나 탈퇴한 지 30일 이상 경과 : 복구 불가
        if (user.getDeletedAt() == null || user.getDeletedAt().isBefore(LocalDateTime.now().minusDays(30))) {
            throw new UserException(ErrorCode.USER_DELETION_EXPIRED_RECOVERY);
        }

        user.setStatus(Status.ACTIVE);
        user.setDeletedAt(null);
        return user;
    }

    // 닉네임 생성기
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