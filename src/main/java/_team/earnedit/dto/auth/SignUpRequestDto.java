package _team.earnedit.dto.auth;

import _team.earnedit.dto.term.TermRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequestDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "Password123!")
    private String password;

    @Schema(description = "약관 동의 항목 리스트")
    private List<TermRequestDto> terms;
}
