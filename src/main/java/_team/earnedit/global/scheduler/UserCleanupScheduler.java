package _team.earnedit.global.scheduler;

import _team.earnedit.entity.User;
import _team.earnedit.entity.User.Status;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteUsersAfter3Years() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(3);
        List<User> usersToDelete = userRepository.findByStatusAndDeletedAtBefore(Status.DELETED, threshold);

        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
            log.info("[hard-delete] {}명의 탈퇴한 사용자를 영구 삭제했습니다.", usersToDelete.size());
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void renameDeletedUserEmailsAfter30Days() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        List<User> targets = userRepository.findByStatusAndDeletedAtBeforeAndEmailNotContaining(
                User.Status.DELETED, threshold, "_deleted_"
        );

        for (User user : targets) {
            String newEmail = user.getEmail() + "_deleted_" + user.getDeletedAt().toLocalDate().toString().replace("-", "");
            user.setEmail(newEmail);
            userRepository.save(user);
        }

        log.info("[탈퇴 30일 초과] {}명의 유저 이메일을 변경했습니다.", targets.size());
    }

}
