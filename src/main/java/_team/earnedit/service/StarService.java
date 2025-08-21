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
        log.debug("[StarService] updateStar 시작 - userId={}, wishId={}", userId, wishId);

        User user = entityFinder.getUserOrThrow(userId);
        Wish wish = entityFinder.getWishOrThrow(wishId);

        // 위시가 star가 아닐때
        if (!wish.isStarred()) {
            log.info("[StarService] Star 추가 시도 - userId={}, wishId={}", userId, wishId);
            validateStarAddLimit(userId);
            addStar(user, wish);
            log.info("[StarService] Star 추가 완료 - userId={}, wishId={}", userId, wishId);
            return true; // star 상태로 변경
        } else {
            log.info("[StarService] Star 제거 시도 - userId={}, wishId={}", userId, wishId);
            deleteStar(userId, wishId, wish);
            reorderRanks(userId);
            log.info("[StarService] Star 제거 완료 및 순위 재정렬 - userId={}", userId);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<StarListResponse> getStarsWish(Long userId) {
        log.debug("[StarService] getStarsWish 시작 - userId={}", userId);
        entityFinder.getUserOrThrow(userId);

        // 정렬된 순서로
        List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);

        log.info("[StarService] Star 리스트 조회 성공 - userId={}", userId);
        return getStarListResponses(stars);
    }

    @Transactional
    public void updateStarOrder(Long userId, List<Long> orderedWishIds) {
        log.debug("[StarService] updateStarOrder 시작 - userId={}", userId);
        validateUserExists(userId); // 유저 존재 여부 검증

        Map<Long, Star> wishIdToStarMap = getStarMap(userId);  // 유저의 Star 목록을 WishId → Star 맵으로 변환

        updateRanks(orderedWishIds, wishIdToStarMap); // 순서대로 Ranking 정렬
        log.info("[StarService] Star 순서 변경 성공 - userId={}", userId);
    }

    // ------------------------------------------ 아래는 메서드 ------------------------------------------ //
    // 최대 5개 제한 검증
    private void validateStarAddLimit(Long userId) {
        int currentCount = starRepository.countByUserId(userId);
        if (currentCount >= 5) {
            log.warn("[StarService] Star 개수 제한 초과 - userId={}, currentCount={}", userId, currentCount);
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

    // 유저 존재 여부 검증
    private void validateUserExists(Long userId) {
        entityFinder.getUserOrThrow(userId);
    }

    // 유저의 Star 목록을 WishId → Star 맵으로 변환
    private Map<Long, Star> getStarMap(Long userId) {
        return starRepository.findByUserId(userId)
                .stream()
                .collect(Collectors.toMap(star -> star.getWish().getId(), Function.identity()));
    }

    // 순서대로 rank 갱신
    private void updateRanks(List<Long> orderedWishIds, Map<Long, Star> starMap) {
        for (int i = 0; i < orderedWishIds.size(); i++) {
            Long wishId = orderedWishIds.get(i);
            Star star = starMap.get(wishId);
            if (star == null) {
                log.error("[StarService] 순서 변경 실패 - wishId={}에 해당하는 Star 없음", wishId);
                throw new StarException(ErrorCode.STAR_NOT_FOUND);
            }
            star.updateRank(i + 1); // rank는 1부터 시작
        }
    }

}
