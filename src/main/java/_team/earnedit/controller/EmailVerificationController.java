package _team.earnedit.controller;

import _team.earnedit.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 요청 (검증코드 포함된 이메일 전송)
    @PostMapping("/send")
    public ResponseEntity<String> sendEmailVerification(@RequestParam String email) {
        emailVerificationService.sendEmailVerification(email);
        return ResponseEntity.ok("인증 이메일이 전송되었습니다.");
    }

    // 이메일 인증 코드 입력 후 검증 (앱 UI에서 코드 입력 후 호출)
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmailToken(@RequestParam String token) {
        emailVerificationService.verifyEmailToken(token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
}
