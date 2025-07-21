package _team.earnedit.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpRequestDto {

    private String email;

    private String password;

    @Builder.Default
    private Boolean isDarkMode = false;

    @Builder.Default
    private Boolean isPublic = false;
}
