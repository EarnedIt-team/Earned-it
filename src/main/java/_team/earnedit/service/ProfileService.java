package _team.earnedit.service;

import _team.earnedit.dto.profile.SalaryRequestDto;
import _team.earnedit.dto.profile.SalaryResponseDto;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.User;
import _team.earnedit.global.util.SalaryCalculator;
import _team.earnedit.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final SalaryRepository salaryRepository;
    private final SalaryCalculator salaryCalculator;

    public SalaryResponseDto updateSalary(long userId, SalaryRequestDto requestDto) {
        Long amount = requestDto.getAmount();
        Integer payday = requestDto.getPayday();
        double amountPerSec = salaryCalculator.calculateAmountPerSec(amount);

        Salary salary = salaryRepository.save(
                Salary.builder()
                        .user(User.builder().id(userId).build())
                        .type(Salary.SalaryType.MONTH)
                        .amount(amount)
                        .tax(false)
                        .amountPerSec(amountPerSec)
                        .payday(payday)
                        .build()
        );

        return SalaryResponseDto.from(salary);
    }

    // 수익 조회용 (예정)
//    public SalaryResponseDto getSalary() {
//
//    }
}
