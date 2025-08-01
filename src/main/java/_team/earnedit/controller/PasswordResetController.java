package _team.earnedit.controller;

import _team.earnedit.dto.auth.PasswordResetRequestDto;
import _team.earnedit.dto.auth.PasswordResetTokenVerifyRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // 이메일 인증 코드 전송
    @PostMapping("/email")
    public ResponseEntity<ApiResponse<String>> sendResetEmail(
            @RequestParam String email)
    {
        passwordResetService.sendPasswordResetEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("비밀번호 재설정 이메일이 전송되었습니다.", email));
    }


    // 인증 코드 검증
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyResetToken(
            @RequestBody PasswordResetTokenVerifyRequestDto requestDto)
    {
        passwordResetService.verifyResetToken(requestDto.getEmail(), requestDto.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("인증이 완료되었습니다."));
    }


    // 비밀번호 변경
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody PasswordResetRequestDto requestDto)
    {
        passwordResetService.resetPassword(requestDto.getEmail(), requestDto.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }

}