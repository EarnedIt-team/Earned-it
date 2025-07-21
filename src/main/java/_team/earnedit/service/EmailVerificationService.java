package _team.earnedit.service;

import _team.earnedit.entity.EmailToken;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.EmailTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailService emailService;
    private final EmailTokenRepository emailTokenRepository;

    // 이메일 인증 요청 (토큰 생성 후 이메일 발송)
    @Transactional
    public void sendEmailVerification(String email) {

        // 기존 토큰 삭제 (이메일당 하나만 존재하도록)
        emailTokenRepository.deleteByEmail(email);
        emailTokenRepository.flush();

        String token = generateToken();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        EmailToken emailToken = EmailToken.builder()
                .email(email)
                .token(token)
                .expiredAt(expiredAt)
                .isVerified(false)
                .build();

        emailTokenRepository.save(emailToken);

        emailService.sendVerificationEmail(email, token);
    }

    // 인증 확인 (토큰 검증)
    @Transactional
    public void verifyEmailToken(String token) {
        EmailToken emailToken = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_TOKEN_NOT_FOUND));

        if (emailToken.isVerified()) {
            throw new UserException(ErrorCode.EMAIL_TOKEN_ALREADY_VERIFIED);
        }

        if (emailToken.isExpired()) {
            throw new UserException(ErrorCode.EMAIL_TOKEN_EXPIRED);
        }

        emailToken.verify();
        emailTokenRepository.save(emailToken);
    }

    // 인증 여부 확인
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        Optional<EmailToken> tokenOpt = emailTokenRepository.findByEmail(email);
        return tokenOpt.map(EmailToken::isVerified).orElse(false);
    }

    // 5자리 숫자 랜덤 토큰 생성
    private String generateToken() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);  // 10000~99999 사이 숫자
        return String.valueOf(number);
    }
}
