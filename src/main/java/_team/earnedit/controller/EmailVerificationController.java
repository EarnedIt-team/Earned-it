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
}
