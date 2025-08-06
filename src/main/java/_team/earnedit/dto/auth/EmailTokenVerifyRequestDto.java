package _team.earnedit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 인증 코드 검증 요청 DTO")
public class EmailTokenVerifyRequestDto {

    @Schema(description = "인증을 요청한 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "이메일로 전송된 인증 코드", example = "12345")
    private String token;
}
