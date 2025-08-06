package _team.earnedit.dto.socialLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Apple 사용자 정보 DTO")
public class AppleUserInfoDto {

    @Schema(description = "사용자 이메일", example = "user@apple.com")
    private String email;

    @Schema(description = "Apple 고유 식별자 (sub)", example = "00123456789abcdef")
    private String sub; // providerId

}
