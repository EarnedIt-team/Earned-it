package _team.earnedit.controller;

import _team.earnedit.dto.auth.PasswordResetRequestDto;
import _team.earnedit.dto.auth.PasswordResetTokenVerifyRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(
            summary = "비밀번호 재설정 이메일 전송",
            description = "입력한 이메일로 비밀번호 재설정용 인증 코드를 보냅니다.",
            parameters = {
                    @Parameter(name = "email", description = "인증 코드를 받을 이메일 주소", required = true)
            }
    )
    @PostMapping("/email")
    public ResponseEntity<ApiResponse<String>> sendResetEmail(
            @RequestParam String email)
    {
        passwordResetService.sendPasswordResetEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("비밀번호 재설정 이메일이 전송되었습니다.", email));
    }

    @Operation(
            summary = "비밀번호 재설정용 인증 코드 검증",
            description = "이메일로 받은 인증 코드를 검증합니다."
    )
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyResetToken(
            @RequestBody PasswordResetTokenVerifyRequestDto requestDto)
    {
        passwordResetService.verifyResetToken(requestDto.getEmail(), requestDto.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("인증이 완료되었습니다."));
    }

    @Operation(
            summary = "비밀번호 재설정",
            description = "이메일과 새 비밀번호를 입력받아 비밀번호를 변경합니다."
    )
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody PasswordResetRequestDto requestDto)
    {
        passwordResetService.resetPassword(requestDto.getEmail(), requestDto.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }

}