package _team.earnedit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "액세스/리프레시 토큰 재발급 응답 DTO")
public class RefreshResponseDto {

    @Schema(description = "새로 발급된 액세스 토큰", example = "eyJhbGciOi...")
    private String accessToken;

    @Schema(description = "갱신된 리프레시 토큰", example = "eyJhbGciOi...")
    private String refreshToken;
}
