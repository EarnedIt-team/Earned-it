package _team.earnedit.service;

import _team.earnedit.dto.profile.SalaryRequestDto;
import _team.earnedit.dto.profile.SalaryResponseDto;
import _team.earnedit.entity.Salary;
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
                        .amount(amount)
                        .build()
        );
        return new SalaryResponseDto();
    }

//    public SalaryResponseDto getSalary() {
//
//    }
}
