package _team.earnedit.global.scheduler;

import _team.earnedit.repository.PuzzleSlotRepository;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class RankScoreCalcScheduler {

    private final UserRepository userRepository;
    private final PuzzleSlotRepository puzzleSlotRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void updateRankScore() {

        userRepository.findAll().forEach(u -> {

        })
    }
}
