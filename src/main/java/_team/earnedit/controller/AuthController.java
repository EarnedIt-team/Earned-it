package _team.earnedit.controller;

import _team.earnedit.dto.auth.*;
import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.socialLogin.KakaoSignInRequestDto;
import _team.earnedit.dto.socialLogin.AppleSignInRequestDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.global.jwt.JwtUtil;
import _team.earnedit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signup(
            @RequestBody @Valid SignUpRequestDto requestDto
    ) {
        SignUpResponseDto responseDto = authService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", responseDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signIn(
            @RequestBody SignInRequestDto requestDto
    ) {
        SignInResponseDto responseDto = authService.signIn(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("로그인이 완료되었습니다.", responseDto));
    }

    @PostMapping("/signin/kakao")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signInWithKakao(
            @RequestBody KakaoSignInRequestDto requestDto
    ) {
        SignInResponseDto responseDto = authService.signInWithKakao(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("카카오 로그인이 완료되었습니다.", responseDto));
    }

    @PostMapping("/signin/apple")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signInWithApple(
            @RequestBody AppleSignInRequestDto requestDto
    ) {
        SignInResponseDto response = authService.signInWithApple(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("애플 로그인이 완료되었습니다.", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refreshToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        RefreshResponseDto responseDto = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("액세스 토큰 재생성과 리프레시 토큰 갱신이 완료되었습니다.", responseDto));
    }



    @Operation(
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @PostMapping("/auth/signout")
    public ResponseEntity<ApiResponse<Void>> signOut(
            HttpServletRequest request,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        String accessToken = jwtUtil.extractBearerPrefix(request.getHeader("Authorization"));
        authService.signOut(userInfoDto.getUserId(), accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("로그아웃이 완료되었습니다."));
    }


}
