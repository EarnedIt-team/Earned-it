package _team.earnedit.service;

import _team.earnedit.dto.star.StarListResponse;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.star.StarException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.mapper.StarMapper;
import _team.earnedit.repository.StarRepository;
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
    private final EntityFinder entityFinder;
    private final StarMapper starMapper;

    @Transactional
    public boolean updateStar(Long userId, long wishId) {
        User user = entityFinder.getUserOrThrow(userId);
        Wish wish = entityFinder.getWishOrThrow(wishId);

        // 위시가 star가 아닐때
        if (!wish.isStarred()) {
            validateStarAddLimit(userId);
            addStar(user, wish);
            return true; // star 상태로 변경
        } else {
            deleteStar(userId, wishId, wish); // Star 제거
            reorderRanks(userId); // Star 삭제된 이후의 rank 재정렬
            return false; // star 상태 x로 변경
        }
    }

    @Transactional(readOnly = true)
    public List<StarListResponse> getStarsWish(Long userId) {
        entityFinder.getUserOrThrow(userId);

        // 정렬된 순서로
        List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);

        return getStarListResponses(stars);
    }

    @Transactional
    public void updateStarOrder(Long userId, List<Long> orderedWishIds) {

        entityFinder.getUserOrThrow(userId);

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

    // 최대 5개 제한 검증
    private void validateStarAddLimit(Long userId) {
        int currentCount = starRepository.countByUserId(userId);
        if (currentCount >= 5) {
            throw new StarException(ErrorCode.TOP_WISH_LIMIT_EXCEEDED);
        }
    }

    // Star 추가 (+ wish 표시)
    private void addStar(User user, Wish wish) {
        int nextRank = starRepository.countByUserId(user.getId()) + 1;

        Star star = Star.builder()
                .user(user)
                .wish(wish)
                .rank(nextRank)
                .build();

        starRepository.save(star);
        wish.setStarred(true);
    }

    private void deleteStar(Long userId, long wishId, Wish wish) {
        starRepository.deleteByUserIdAndWishId(userId, wishId);
        wish.setStarred(false);
    }


    // 순위 재정렬(1부터 연속)
    private void reorderRanks(Long userId) {
        List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setRank(i + 1);
        }
    }

    // Star 목록 응답 객체 생성
    private List<StarListResponse> getStarListResponses(List<Star> stars) {
        return stars.stream()
                .map(star -> {
                    Wish wish = star.getWish();
                    return starMapper.toStarListResponse(star, wish);
                })
                .collect(Collectors.toList());
    }

}
