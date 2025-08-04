package _team.earnedit.dto.profile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameRequestDto {

    private String nickname;

    @Builder
    public NicknameRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
