package _team.earnedit.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "닉네임 변경 요청 DTO")
public class NicknameRequestDto {

    @Schema(description = "변경할 닉네임", example = "요림짱")
    private String nickname;

    @Builder
    public NicknameRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
