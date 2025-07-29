package _team.earnedit.dto.socialLogin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppleUserInfoDto {

    private String email;

    private String sub; // providerId

}
