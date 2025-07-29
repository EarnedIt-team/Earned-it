package _team.earnedit.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponseDto {

    private String accessToken;

    private String refreshToken;

    private Long userId;

    private boolean isSignUp = false;

}