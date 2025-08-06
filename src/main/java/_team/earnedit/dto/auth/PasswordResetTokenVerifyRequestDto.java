package _team.earnedit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 재설정용 인증 코드 검증 DTO")
public class PasswordResetTokenVerifyRequestDto {

    @Schema(description = "사용자 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "전송받은 인증 코드", example = "54321")
    private String token;

}
