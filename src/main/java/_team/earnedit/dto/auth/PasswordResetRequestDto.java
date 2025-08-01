package _team.earnedit.dto.auth;

import lombok.Getter;

@Getter
public class PasswordResetRequestDto {

    private String email;

    private String newPassword;

}
