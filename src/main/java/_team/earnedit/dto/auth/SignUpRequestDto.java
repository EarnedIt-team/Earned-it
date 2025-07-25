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

    private Boolean isDarkMode;

    private Boolean isPublic;

    private List<TermRequestDto> terms;
}
