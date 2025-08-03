package _team.earnedit.service;

import _team.earnedit.dto.star.StarListResponse;
import _team.earnedit.dto.star.StarOrderUpdateRequest;
import _team.earnedit.entity.Star;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StarService {
    private final StarRepository starRepository;
    private final WishRepository wishRepository;
    private final UserRepository userRepository;


    @Transactional
    public boolean updateStar(Long userId, long wishId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        boolean isStarred = wish.isStarred();

        if (!isStarred) {
            // Star 추가 시 검증
            int currentStarCount = starRepository.countByUserId(userId);
            if (currentStarCount >= 5) {
                throw new StarException(ErrorCode.TOP_WISH_LIMIT_EXCEEDED);
            }

            // Star 순위는 현재 별표 개수 + 1
            Star star = Star.builder()
                    .user(user)
                    .wish(wish)
                    .rank(currentStarCount + 1)
                    .build();

            starRepository.save(star);
            wish.setStarred(true);
            return true;
        } else {
            // Star 제거
            starRepository.deleteByUserIdAndWishId(userId, wishId);
            wish.setStarred(false);

            // Stra 삭제된 이후의 rank 재정렬
            List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);
            for (int i = 0; i < stars.size(); i++) {
                stars.get(i).setRank(i + 1); // 순위 재정렬
            }
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<StarListResponse> getStarsWish(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 정렬된 순서로
        List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);

        // 조회된 Star가 없을 때
        if (stars.isEmpty()) {
            throw new StarException(ErrorCode.TOP_WISH_EMPTY);
        }

        return stars.stream()
                .map(star -> {
                    Wish wish = star.getWish();
                    return StarListResponse.builder()
                            .starId(star.getId())
                            .wishId(wish.getId())
                            .userId(star.getUser().getId())
                            .name(wish.getName())
                            .rank(star.getRank())
                            .itemImage(wish.getItemImage())
                            .vendor(wish.getVendor())
                            .price(wish.getPrice())
                            .rank(star.getRank())
                            .isBought(wish.isBought())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStarOrder(Long userId, List<Long> orderedWishIds) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<Star> stars = starRepository.findByUserId(userId);

        // WishId → Star 매핑
        Map<Long, Star> wishIdToStarMap = stars.stream()
                .collect(Collectors.toMap(star -> star.getWish().getId(), Function.identity()));

        // 순서대로 rank 갱신
        for (int i = 0; i < orderedWishIds.size(); i++) {
            Long wishId = orderedWishIds.get(i);
            Star star = wishIdToStarMap.get(wishId);
            if (star != null) {
                star.updateRank(i + 1); // 1부터 시작
            } else {
                throw new StarException(ErrorCode.STAR_NOT_FOUND);
            }
        }
    }
}
