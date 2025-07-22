package _team.earnedit.global.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SalaryCalculator {

    public long getCurrentMonthSeconds() {
        LocalDate now = LocalDate.now();
        int daysInMonth = now.lengthOfMonth();
        return daysInMonth * 24L * 60L * 60L;
    }

    public double calculateAmountPerSec(Long amount) {
        if (amount == null || amount == 0) {
            return 0.0;
        }
        long secondsInMonth = getCurrentMonthSeconds();
        return (double) amount / secondsInMonth;
    }
}
