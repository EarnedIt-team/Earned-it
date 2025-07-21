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
                <p>아래 인증 코드를 입력하거나 버튼을 눌러 인증을 완료하세요.</p>

                <div style="font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0;">
                    %s
                </div>

                <a href='%s' 
                   style="display: inline-block; padding: 10px 20px; font-size: 16px;
                          color: white; background-color: #4CAF50; text-decoration: none;
                          border-radius: 5px;">
                    이메일 인증하기
                </a>

                <p style="margin-top: 30px; font-size: 12px; color: #999;">
                    만약 버튼이 동작하지 않는다면 아래 링크를 복사해서 브라우저에 붙여넣어주세요.<br/>
                    %s
                </p>
            </div>
            """.formatted(token, verifyLink, verifyLink);

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
