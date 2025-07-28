package _team.earnedit.dto.socialLogin;

import lombok.Getter;

@Getter
public class AppleUserInfoDto {
    private final String email;

    public AppleUserInfoDto(String email) {
        this.email = email;
    }

}