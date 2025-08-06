package _team.earnedit.dto.socialLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "카카오 로그인 요청 DTO")
public class KakaoSignInRequestDto {

    @Schema(description = "카카오 액세스 토큰", example = "kakao_access_token_abcdef123")
    private String accessToken;

}
