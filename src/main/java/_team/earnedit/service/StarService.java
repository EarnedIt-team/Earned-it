package _team.earnedit.service;

import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.star.StarException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.exception.wish.WishException;
import _team.earnedit.repository.StarRepository;
import _team.earnedit.repository.UserRepository;
import _team.earnedit.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StarService {
    private final StarRepository starRepository;
    private final WishRepository wishRepository;
    private final UserRepository userRepository;


    @Transactional
    public void updateStar(Long userId, long wishId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));


        boolean isStarred = wish.isStarred();

        if (!isStarred) {
            // 추가 시 검증
            int currentStarCount = wishRepository.countStarredByUserId(userId);
            if (currentStarCount >= 5) {
                throw new StarException(ErrorCode.TOP_WISH_LIMIT_EXCEEDED);
            }
            wish.setStarred(true);
        } else {
            // 제거는 자유
            wish.setStarred(false);
        }
    }
}
