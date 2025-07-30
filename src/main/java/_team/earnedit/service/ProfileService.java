package _team.earnedit.service;

import _team.earnedit.dto.profile.SalaryRequestDto;
import _team.earnedit.dto.profile.SalaryResponseDto;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.salary.SalaryException;
import _team.earnedit.global.util.SalaryCalculator;
import _team.earnedit.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final SalaryRepository salaryRepository;
    private final SalaryCalculator salaryCalculator;

    // 수익 정보 입력 + 수정 (덮어쓰기)
    @Transactional
    public SalaryResponseDto updateSalary(long userId, SalaryRequestDto requestDto) {
        Long amount = requestDto.getAmount();
        Integer payday = requestDto.getPayday();
        double amountPerSec = salaryCalculator.calculateAmountPerSec(amount);

        Optional<Salary> existing = salaryRepository.findByUserId(userId);

        Salary salary = existing
                .map(s -> {
                    s.setAmount(amount);
                    s.setPayday(payday);
                    s.updateAmountPerSec(amountPerSec);
                    s.setTax(false);
                    s.setType(Salary.SalaryType.MONTH);
                    return s;
                })
                .orElseGet(() -> Salary.builder()
                        .user(User.builder().id(userId).build())
                        .type(Salary.SalaryType.MONTH)
                        .amount(amount)
                        .tax(false)
                        .amountPerSec(amountPerSec)
                        .payday(payday)
                        .build());

        Salary saved = salaryRepository.save(salary);
        return SalaryResponseDto.from(saved);
    }

    // 수익 조회
    @Transactional(readOnly = true)
    public SalaryResponseDto getSalary(Long userId) {
        Salary salary = salaryRepository.findByUserId(userId)
                .orElseThrow(() -> new SalaryException(ErrorCode.SALARY_NOT_FOUND));

        return SalaryResponseDto.from(salary);
    }

}
