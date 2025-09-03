package _team.earnedit.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "프로필 공개범위 변경 요청 DTO")
public class UpdateVisibilityRequestDto {

    @NotNull
    @Schema(description = "프로필 공개 여부 (true=공개, false=비공개)", example = "true")
    private Boolean isPublic;
}