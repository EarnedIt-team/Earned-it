package _team.earnedit.dto.socialLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "애플 로그인 요청 DTO")
public class AppleSignInRequestDto {

    @Schema(description = "Apple에서 발급한 ID 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String idToken;

}