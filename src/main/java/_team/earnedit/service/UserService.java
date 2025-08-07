package _team.earnedit.service;

import _team.earnedit.entity.User;
import _team.earnedit.entity.User.Status;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.util.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EntityFinder entityFinder;

    @Transactional
    public void softDeleteUser(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);

        if (user.getStatus() == Status.DELETED) {
            throw new UserException(ErrorCode.USER_ALREADY_DELETED);
        }

        user.softDeleted();

        redisTemplate.delete("refresh:" + userId);
    }
}
