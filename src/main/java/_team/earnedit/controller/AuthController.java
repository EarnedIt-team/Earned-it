package _team.earnedit.controller;

import _team.earnedit.dto.auth.SignUpRequestDto;
import _team.earnedit.dto.auth.SignUpResponseDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.AuthService;
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
}
