package _team.earnedit.dto.profile;

import _team.earnedit.entity.Salary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalaryResponseDto {

    private Long amount;
    private Double amountPerSec;
    private Integer payday;

    public static SalaryResponseDto from(Salary salary) {
        return SalaryResponseDto.builder()
                .amount(salary.getAmount())
                .amountPerSec(salary.getAmountPerSec())
                .payday(salary.getPayday())
                .build();
    }
}