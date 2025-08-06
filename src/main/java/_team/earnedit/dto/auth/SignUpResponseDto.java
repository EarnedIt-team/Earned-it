package _team.earnedit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class SignUpResponseDto {

    @Schema(description = "생성된 사용자 ID", example = "1")
    private Long id;

    @Schema(description = "가입한 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "기본 닉네임", example = "익명의사용자12343")
    private String nickname;
}
