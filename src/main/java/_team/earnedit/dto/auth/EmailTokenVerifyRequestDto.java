package _team.earnedit.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailTokenVerifyRequestDto {
    private String email;
    private String token;
}
