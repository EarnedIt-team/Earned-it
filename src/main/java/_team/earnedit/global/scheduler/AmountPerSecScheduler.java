package _team.earnedit.global.scheduler;

import _team.earnedit.entity.Salary;
import _team.earnedit.global.util.SalaryCalculator;
import _team.earnedit.repository.SalaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AmountPerSecScheduler {

    private final SalaryRepository salaryRepository;
    private final SalaryCalculator salaryCalculator;

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void updateSalaryPerSecForAllUsers() {
        List<Salary> salaries = salaryRepository.findAll();

        for (Salary salary : salaries) {
            double newSalaryPerSec = salaryCalculator.calculateAmountPerSec(salary.getAmount());
            salary.updateAmountPerSec(newSalaryPerSec);
        }

        log.info("[스케줄러] 모든 유저 salaryPerSec 갱신 완료");
    }
}
