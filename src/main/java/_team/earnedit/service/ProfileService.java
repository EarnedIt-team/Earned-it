package _team.earnedit.service;

import _team.earnedit.dto.profile.SalaryRequestDto;
import _team.earnedit.dto.profile.SalaryResponseDto;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.User;
import _team.earnedit.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final SalaryRepository salaryRepository;

    public SalaryResponseDto updateSalary(long userId, SalaryRequestDto requestDto) {
        Long amount = requestDto.getAmount();

        Salary salary = salaryRepository.save(
                Salary.builder()
                        .user(User.builder().id(userId).build())  // 연관관계 설정 (프록시로 처리)
                        .type(Salary.SalaryType.MONTH)            // 무조건 MONTH
                        .amount(amount)                           // 월 수령액
                        .tax(false)                               // 무조건 false
                        .amountPerSec(calculateSalaryPerSec(amount)) // 계산해서 저장
                        .build()
        );

        return new SalaryResponseDto();
    }

    private double calculateSalaryPerSec(Long amount) {
        if (amount == null || amount == 0) {
            return 0.0;
        }
        long secondsInMonth = 30L * 24L * 60L * 60L;  // 월 기준 30일
        return (double) amount / secondsInMonth;
    }

    // 수익 조회용 (예정)
//    public SalaryResponseDto getSalary() {
//
//    }
}
