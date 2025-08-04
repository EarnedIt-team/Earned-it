package _team.earnedit.service;

import _team.earnedit.dto.wish.*;
import _team.earnedit.entity.QWish;
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
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final FileUploadService fileUploadService;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public WishAddResponse addWish(WishAddRequest wishAddRequest, Long userId, MultipartFile itemImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 이미지 업로드 처리
        String imageUrl = fileUploadService.uploadFile(itemImage);

        boolean isStarred = wishAddRequest.isStarred();

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
                .itemImage(imageUrl)
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


    // Todo 페이지 네이션 작업해야함
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
                        .wishId(wish.getId())
                        .userId(wish.getUser().getId())
                        .name(wish.getName())
                        .price(wish.getPrice())
                        .itemImage(wish.getItemImage())
                        .isBought(wish.isBought())
                        .vendor(wish.getVendor())
                        .createdAt(wish.getCreatedAt())
                        .isStarred(wish.isStarred())
                        .url(wish.getUrl())
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

        starRepository.deleteByWishId(wishId);
        wishRepository.deleteById(wishId);
    }

    @Transactional(readOnly = true)
    public WishDetailResponse getWish(Long wishId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        return WishDetailResponse.builder()
                .wishId(wish.getId())
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
                        .wishId(wish.getId())
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

    /**
     * 사용자의 위시리스트를 조건에 맞게 검색합니다.
     * 필터 조건: 키워드(이름/회사명), 구매여부, 별표 여부
     * 정렬 조건: 이름, 회사명, 금액, 생성일
     */
    @Transactional(readOnly = true)
    public List<WishListResponse> searchWish(Long userId, WishSearchCondition cond) {
        QWish wish = QWish.wish;

        // 조건 빌더 초기화 - 사용자 ID로 기본 필터 설정
        BooleanBuilder builder = new BooleanBuilder()
                .and(wish.user.id.eq(userId));

        // 키워드 검색 (이름 또는 회사명에 포함된 경우)
        if (cond.getKeyword() != null && !cond.getKeyword().isBlank()) {
            builder.and(
                    wish.name.containsIgnoreCase(cond.getKeyword())
                            .or(wish.vendor.containsIgnoreCase(cond.getKeyword()))
            );
        }

        // 구매 여부 필터
        if (cond.getIsBought() != null) {
            builder.and(wish.isBought.eq(cond.getIsBought()));
        }

        // 별표 여부 필터
        if (cond.getIsStarred() != null) {
            builder.and(wish.isStarred.eq(cond.getIsStarred()));
        }

        // 정렬 기준 계산
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(cond);

        // QueryDSL 쿼리 실행
        List<Wish> results = queryFactory
                .selectFrom(wish)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        if (results.isEmpty()) {
            throw new WishException(ErrorCode.NOT_FOUND_SEARCH_RESULT);
        }

        // 결과를 DTO로 매핑 후 반환
        return results.stream()
                .map(WishListResponse::from)
                .toList();
    }

    /**
     * 검색 조건에 따라 정렬 기준을 반환하는 유틸 메서드
     * @param cond WishSearchCondition 검색 조건 객체
     * @return OrderSpecifier 정렬 기준 객체
     */
    private OrderSpecifier<?> getOrderSpecifier(WishSearchCondition cond) {
        // 정렬 방향 파싱 (asc/desc), 기본값은 DESC
        Sort.Direction direction = Sort.Direction.fromOptionalString(cond.getDirection())
                .orElse(Sort.Direction.DESC);

        // Wish 엔티티 기준 PathBuilder 생성
        PathBuilder<Wish> sortPath = new PathBuilder<>(Wish.class, "wish");
        String sortField = cond.getSort();

        // 필드명별 정렬 기준 분기 처리
        return switch (sortField) {
            case "name" -> direction.isAscending()
                    ? sortPath.getString("name").asc()
                    : sortPath.getString("name").desc();
            case "vendor" -> direction.isAscending()
                    ? sortPath.getString("vendor").asc()
                    : sortPath.getString("vendor").desc();
            case "price" -> direction.isAscending()
                    ? sortPath.getNumber("price", Integer.class).asc()
                    : sortPath.getNumber("price", Integer.class).desc();
            case "createdAt" -> direction.isAscending()
                    ? sortPath.getDateTime("createdAt", java.time.LocalDateTime.class).asc()
                    : sortPath.getDateTime("createdAt", java.time.LocalDateTime.class).desc();
            default -> throw new IllegalArgumentException("정렬할 수 없는 필드입니다: " + sortField);
        };
    }
}
