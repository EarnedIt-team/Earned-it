package _team.earnedit.service;

import _team.earnedit.entity.User;
import _team.earnedit.entity.User.Status;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == Status.DELETED) {
            throw new UserException(ErrorCode.USER_ALREADY_DELETED);
        }

        user.softDeleted();

        redisTemplate.delete("refresh:" + userId);
    }
}
