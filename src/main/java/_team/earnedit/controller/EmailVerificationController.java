package _team.earnedit.controller;

import _team.earnedit.dto.auth.EmailTokenVerifyRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 요청 (검증코드 포함된 이메일 전송)
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendEmailVerification(@RequestParam String email) {
        emailVerificationService.sendEmailVerification(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("인증 이메일이 전송되었습니다.", email));
    }

    // 이메일 인증 코드 입력 후 검증 (앱 UI에서 코드 입력 후 호출)
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmailToken(@RequestBody EmailTokenVerifyRequestDto requestDto) {
        emailVerificationService.verifyEmailToken(requestDto.getEmail(), requestDto.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }
}
