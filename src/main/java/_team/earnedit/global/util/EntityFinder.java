package _team.earnedit.global.util;

import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.exception.wish.WishException;
import _team.earnedit.repository.UserRepository;
import _team.earnedit.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFinder {

    private final UserRepository userRepository;
    private final WishRepository wishRepository;

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public Wish getWishOrThrow(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));
    }

}
