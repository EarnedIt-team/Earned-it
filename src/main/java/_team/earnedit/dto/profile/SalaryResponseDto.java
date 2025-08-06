package _team.earnedit.dto.profile;

import _team.earnedit.entity.Salary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "수익 정보 응답 DTO")
public class SalaryResponseDto {

    @Schema(description = "월 수익", example = "4000000")
    private Long amount;

    @Schema(description = "초당 수익", example = "1.532")
    private Double amountPerSec;

    @Schema(description = "월급 지급일 (1~31)", example = "25")
    private Integer payday;

    public static SalaryResponseDto from(Salary salary) {
        return SalaryResponseDto.builder()
                .amount(salary.getAmount())
                .amountPerSec(salary.getAmountPerSec())
                .payday(salary.getPayday())
                .build();
    }
}