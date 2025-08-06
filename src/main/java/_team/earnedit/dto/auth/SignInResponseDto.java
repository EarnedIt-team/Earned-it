package _team.earnedit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class SignInResponseDto {

    @Schema(description = "발급된 액세스 토큰", example = "eyJhbGciOi...")
    private String accessToken;

    @Schema(description = "발급된 리프레시 토큰", example = "eyJhbGciOi...")
    private String refreshToken;

    @Schema(description = "로그인한 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "약관 동의 여부", example = "true")
    private boolean hasAgreedTerm;

}