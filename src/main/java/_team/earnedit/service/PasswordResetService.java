package _team.earnedit.service;

import _team.earnedit.entity.PasswordResetToken;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.PasswordResetTokenRepository;
import _team.earnedit.repository.UserRepository;
import _team.earnedit.global.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // 비밀번호 재설정 인증 요청
    @Transactional
    public void sendPasswordResetEmail(String email) {
        EmailUtils.validateEmailFormat(email);

        User user = userRepository.findByEmailAndProvider(email, User.Provider.LOCAL)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 기존 토큰 삭제 (이메일당 하나만 존재하도록)
        tokenRepository.deleteByEmail(email);
        tokenRepository.flush();

        String token = EmailUtils.generateToken();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiredAt(expiredAt)
                .used(false)
                .build();

        tokenRepository.save(resetToken);
        emailService.sendVerificationEmail(email, token);
    }

}
