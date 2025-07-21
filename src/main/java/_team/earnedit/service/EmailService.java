package _team.earnedit.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
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
        String subject = "[서비스명] 이메일 인증 코드";
        String html = "인증 코드 : " + token + "<br/>"
                + "<a href='" + verifyLink + "'>여기를 클릭해서 인증하세요</a>";

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
