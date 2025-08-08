package _team.earnedit.global.scheduler;

import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckInResetScheduler {

    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void resetDailyCheckIn() {
        int updated = userRepository.resetAllCheckedIn();

        log.info("[CheckInResetScheduler] {}명의 출석체크 상태를 초기화했습니다.", updated);
    }
}
