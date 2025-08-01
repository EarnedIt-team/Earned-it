package _team.earnedit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.verification.url}")
    private String emailVerificationUrl;

    public void sendVerificationEmail(String email, String token) {
        String verifyLink = emailVerificationUrl + "?token=" + token;
        String subject = "[earned It !] 이메일 인증 안내";

        String html = """
        <div style="font-family: Arial, sans-serif; font-size: 16px; color: #333;">
            <h2>이메일 인증 코드</h2>
            <p>아래 인증 코드를 입력해서 인증을 완료하세요.</p>

            <div style="font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0;">
                %s
            </div>

            <p style="margin-top: 30px; font-size: 12px; color: #999;">
                인증 코드는 15분 동안만 유효합니다.<br/>
                인증 링크: %s
            </p>
        </div>
        """.formatted(token, verifyLink);

        sendHtml(email, subject, html);
    }

    public void sendPasswordResetEmail(String email, String token) {
        String subject = "[earned It !] 비밀번호 재설정 인증 코드 안내";

        String html = """
        <div style="font-family: Arial, sans-serif; font-size: 16px; color: #333;">
            <h2>비밀번호 재설정 인증 코드</h2>
            <p>아래 인증 코드를 입력해서 비밀번호를 재설정하세요.</p>

            <div style="font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0;">
                %s
            </div>

            <p style="margin-top: 30px; font-size: 12px; color: #999;">
                인증 코드는 15분 동안만 유효합니다.
            </p>
        </div>
        """.formatted(token);

        sendHtml(email, subject, html);
    }


    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
