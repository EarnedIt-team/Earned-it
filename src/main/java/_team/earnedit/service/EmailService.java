package _team.earnedit.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String TITLE_COLOR = "#EB5E70"; // 이메일 인증
    private static final String CODE_COLOR   = "#4E4E4E";    // 코드 텍스트

    private static final String LOGO_CID = "logo"; // HTML 내 <img src="cid:logo">

    // 이메일 인증메일 전송
    public void sendVerificationEmail(String email, String token) {
        String subject = "[Earned !t] 이메일 인증 안내";
        String title   = "반가워요! 언드잇 팀입니다.";
        String body1   = "앱의 코드 입력란에 아래 인증 코드를 입력해서 인증을 완료해주세요.";
        String html = composeHtml(title, body1, token, TITLE_COLOR);
        sendHtmlWithLogo(email, subject, html);
    }

    // 비밀번호 재설정 메일 전송
    public void sendPasswordResetEmail(String email, String token) {
        String subject = "[Earned !t] 비밀번호 재설정 안내";
        String title   = "언드잇 팀이 재방문을 환영합니다.";
        String body1   = "앱의 코드 입력란에 아래 인증 코드를 입력해서 비밀번호를 재설정하세요.";
        String html = composeHtml(title, body1, token, TITLE_COLOR);
        sendHtmlWithLogo(email, subject, html);
    }

    // 공통 HTML 템플릿
    private String composeHtml(String title, String subtitle, String token, String titleColor) {
        return """
        <div style="font-family: Arial, sans-serif; font-size:16px; color:#333; text-align:center; padding:28px 20px; background:#ffffff;">
            <div style="display:inline-block; padding:22px 26px; border-radius:16px; box-shadow:0 8px 24px rgba(0,0,0,0.06); border:1px solid #f0f0f0;">
                <img src="cid:%s" alt="Earned !t" style="max-width:180px; height:auto; margin:8px auto 18px; display:block;">
                <h2 style="color:%s; margin:0 0 8px; font-size:22px;">%s</h2>
                <p style="margin:0 0 20px; color:#373737; line-height:1.6;">%s</p>

                <div style="display:inline-block; padding:14px 28px; background:#f7f7f8; border:1px dashed #e6e6e8; border-radius:10px; font-size:26px; font-weight:800; letter-spacing:2px; color:%s;">
                    %s
                </div>

                <p style="margin:22px 0 0; font-size:12px; color:#999;">
                    인증 코드는 15분 동안만 유효합니다.
                </p>
            </div>
        </div>
        """.formatted(LOGO_CID, titleColor, title, subtitle, CODE_COLOR, token);
    }

    // 로고 포함
    private void sendHtmlWithLogo(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            helper.addInline(LOGO_CID,
                    new ClassPathResource("static/images/logo.png"),
                    "image/png");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
