package _team.earnedit.service;

import _team.earnedit.dto.wish.*;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final StarRepository starRepository;

    @Transactional
    public WishAddResponse addWish(WishAddRequest wishAddRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        boolean isStarred = wishAddRequest.isStarred();
        log.info("isStarred {}", isStarred);

        // Top 5 초과 예외 처리
        if (isStarred) {
            int currentStarCount = starRepository.countByUserId(userId);
            if (currentStarCount >= 5) {
                throw new StarException(ErrorCode.TOP_WISH_LIMIT_EXCEEDED);
            }
        }

        // wish 객체 생성
        Wish wish = Wish.builder()
                .user(user)
                .price(wishAddRequest.getPrice())
                .url(wishAddRequest.getUrl())
                .itemImage(wishAddRequest.getItemImage())
                .name(wishAddRequest.getName())
                .vendor(wishAddRequest.getVendor())
                .isStarred(isStarred)
                .build();

        wishRepository.save(wish);

        // 별표 로직 분기
        if (isStarred) {
            int currentStarCount = starRepository.countByUserId(userId);  // 다시 조회하거나 변수 재사용
            Star star = Star.builder()
                    .user(user)
                    .wish(wish)
                    .rank(currentStarCount + 1)
                    .build();
            starRepository.save(star);
        }

        return WishAddResponse.builder()
                .wishId(wish.getId())
                .createdAt(wish.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<WishListResponse> getWishList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 이름 순서로 조회
        List<Wish> wishList = wishRepository.findByUserIdOrderByNameAsc(userId);

        if (wishList.isEmpty()) {
            throw new WishException(ErrorCode.WISHLIST_EMPTY);
        }

        return wishList.stream()
                .map(wish -> WishListResponse.builder()
                        .id(wish.getId())
                        .userId(wish.getUser().getId())
                        .name(wish.getName())
                        .price(wish.getPrice())
                        .itemImage(wish.getItemImage())
                        .isBought(wish.isBought())
                        .vendor(wish.getVendor())
                        .createdAt(wish.getCreatedAt())
                        .isStarred(wish.isStarred())
                        .build())
                .toList();

    }

    @Transactional
    public WishUpdateResponse updateWish(WishUpdateRequest wishUpdateRequest, Long userId, Long wishId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId).orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        // 다른 사용자의 수정 시도에 대한 예외처리
        if (!wish.getUser().getId().equals(userId)) {
            throw new WishException(ErrorCode.WISH_UPDATE_FORBIDDEN);
        }

        wish.update(
                wishUpdateRequest.getName(),
                wishUpdateRequest.getPrice(),
                wishUpdateRequest.getItemImage(),
                wishUpdateRequest.getVendor(),
                wishUpdateRequest.getUrl()
        );

        return WishUpdateResponse.builder()
                .wishId(wish.getId())
                .name(wish.getName())
                .ItemImage(wish.getItemImage())
                .vendor(wish.getVendor())
                .price(wish.getPrice())
                .url(wish.getUrl())
                .updatedAt(wish.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteWish(Long wishId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        // 다른 사용자의 삭제 시도에 대한 예외처리
        if (!wish.getUser().getId().equals(userId)) {
            throw new WishException(ErrorCode.WISH_DELETE_FORBIDDEN);
        }

        wishRepository.delete(wish);
    }

    @Transactional(readOnly = true)
    public WishDetailResponse getWish(Long wishId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        return WishDetailResponse.builder()
                .id(wish.getId())
                .name(wish.getName())
                .price(wish.getPrice())
                .itemImage(wish.getItemImage())
                .isBought(wish.isBought())
                .vendor(wish.getVendor())
                .createdAt(wish.getCreatedAt())
                .updatedAt(wish.getUpdatedAt())
                .isStarred(wish.isStarred())
                .url(wish.getUrl())
                .build();

    }

    @Transactional
    public boolean toggleBoughtStatus(Long wishId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        wish.setBought(!wish.isBought());

        return wish.isBought();
    }

    @Transactional(readOnly = true)
    public List<WishDetailResponse> highlightWish(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<Wish> wishList = wishRepository.findByUserIdOrderByNameAsc(userId);

        if (wishList.isEmpty()) {
            throw new WishException(ErrorCode.WISHLIST_EMPTY);
        }

        return wishList.stream()
                .limit(3) // 3개만 조회
                .map(wish -> WishDetailResponse.builder()
                        .id(wish.getId())
                        .userId(wish.getUser().getId())
                        .name(wish.getName())
                        .price(wish.getPrice())
                        .itemImage(wish.getItemImage())
                        .isBought(wish.isBought())
                        .vendor(wish.getVendor())
                        .createdAt(wish.getCreatedAt())
                        .isStarred(wish.isStarred())
                        .build())
                .toList();

    }
}
