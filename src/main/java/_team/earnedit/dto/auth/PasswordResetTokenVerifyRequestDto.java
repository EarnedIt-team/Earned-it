package _team.earnedit.dto.auth;

import lombok.Getter;

@Getter
public class PasswordResetTokenVerifyRequestDto {

    private String email;

    private String token;

}
