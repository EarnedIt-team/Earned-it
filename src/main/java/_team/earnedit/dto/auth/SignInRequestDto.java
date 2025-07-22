package _team.earnedit.dto.auth;

import lombok.Getter;

@Getter
public class SignInRequestDto {
    private String email;
    private String password;
}