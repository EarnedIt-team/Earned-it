package _team.earnedit.controller;

import _team.earnedit.dto.auth.EmailTokenVerifyRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "이메일 인증 요청",
            description = "입력한 이메일로 인증 코드를 포함한 이메일을 발송합니다.",
            parameters = {
                    @Parameter(name = "email", description = "인증 이메일을 보낼 대상 이메일 주소", required = true)
            }
    )
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendEmailVerification(@RequestParam String email) {
        emailVerificationService.sendEmailVerification(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("인증 이메일이 전송되었습니다.", email));
    }

    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "사용자가 입력한 인증 코드를 서버에서 검증합니다. 인증이 완료되어야 회원가입이 가능해집니다."
    )
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmailToken(
            @RequestBody EmailTokenVerifyRequestDto requestDto
    ) {
        emailVerificationService.verifyEmailToken(requestDto.getEmail(), requestDto.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }
}
