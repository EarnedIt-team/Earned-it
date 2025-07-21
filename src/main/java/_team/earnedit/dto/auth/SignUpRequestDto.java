package _team.earnedit.dto.auth;

import _team.earnedit.dto.term.TermRequestDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SignUpRequestDto {

    private String email;

    private String password;

    @Builder.Default
    private Boolean isDarkMode = false;

    @Builder.Default
    private Boolean isPublic = false;

    private List<TermRequestDto> terms;
}
