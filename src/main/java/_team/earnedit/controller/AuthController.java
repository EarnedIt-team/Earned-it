package _team.earnedit.controller;

import _team.earnedit.dto.auth.*;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signup(@RequestBody @Valid SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = authService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", responseDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signIn(@RequestBody SignInRequestDto requestDto) {
        SignInResponseDto responseDto = authService.signIn(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("로그인이 완료되었습니다.", responseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refreshToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        RefreshResponseDto response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("액세스 토큰 재생성과 리프레시 토큰 갱신이 완료되었습니다.", response));
    }
}
