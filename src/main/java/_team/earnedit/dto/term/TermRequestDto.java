package _team.earnedit.dto.term;

import _team.earnedit.entity.Term;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "약관 동의 요청 DTO")
public class TermRequestDto {

    @Schema(description = "약관 유형", example = "SERVICE_REQUIRED")
    private Term.Type type;

    @Schema(description = "체크 여부", example = "true")
    private boolean isChecked;

}
