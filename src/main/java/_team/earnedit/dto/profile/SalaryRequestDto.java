package _team.earnedit.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "수익 정보 저장/수정 요청 DTO")
public class SalaryRequestDto {

    @Schema(description = "월급 금액", example = "4000000")
    private Long amount;

    @Schema(description = "월급 지급일 (1~31)", example = "25")
    private Integer payday;

}
