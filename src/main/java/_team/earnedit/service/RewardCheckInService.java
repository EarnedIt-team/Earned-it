package _team.earnedit.service;

import _team.earnedit.entity.User;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardCheckInService {
    private final UserRepository userRepository;
    private final EntityFinder entityFinder;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkInUser(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);
        user.checkIn();
        userRepository.save(user);
    }

    // 출석 시 점수 제공 (+10pt) & 트랜잭션 분리
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void giveAttendanceScore(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);

        user.addScore(10);
        userRepository.save(user);
    }
}
