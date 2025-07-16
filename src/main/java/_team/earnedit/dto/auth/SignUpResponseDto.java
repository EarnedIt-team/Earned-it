package _team.earnedit.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponseDto {
    private Long id;
    private String email;
    private String nickname;
}
