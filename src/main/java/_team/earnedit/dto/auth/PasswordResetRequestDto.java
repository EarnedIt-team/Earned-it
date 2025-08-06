package _team.earnedit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 재설정 요청 DTO")
public class PasswordResetRequestDto {

    @Schema(description = "사용자 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "새 비밀번호", example = "NewPassword123!")
    private String newPassword;

}
