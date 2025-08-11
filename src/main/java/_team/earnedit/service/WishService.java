package _team.earnedit.service;

import _team.earnedit.dto.PagedResponse;
import _team.earnedit.dto.wish.*;
import _team.earnedit.entity.QWish;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.star.StarException;
import _team.earnedit.global.exception.wish.WishException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.mapper.WishMapper;
import _team.earnedit.repository.StarRepository;
import _team.earnedit.repository.WishRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishService {

    public static final int MAX_WISH_COUNT = 100;
    private final WishRepository wishRepository;
    private final StarRepository starRepository;
    private final FileUploadService fileUploadService;
    private final JPAQueryFactory queryFactory;
    private final EntityFinder entityFinder;
    private final WishMapper wishMapper;

    @Transactional
    public WishAddResponse addWish(WishAddRequest wishAddRequest, Long userId, MultipartFile itemImage) {
        // 1. 사용자 조회
        User user = entityFinder.getUserOrThrow(userId);

        // 2. 위시 개수 제한 검증
        validateWishLimit(user);

        // 3. 이미지 업로드 처리
        String imageUrl = fileUploadService.uploadFile(itemImage);

        // 4. 별표 여부 확인 및 Top 5 제한 검증
        boolean isStarred = wishAddRequest.isStarred();
        if (isStarred) {
            validateStarLimit(userId);
        }

        // 5. Wish 엔티티 생성 및 저장
        Wish wish = wishMapper.toEntity(wishAddRequest, user, imageUrl, isStarred);
        wishRepository.save(wish);

        // 6. 별표 Star 엔티티 추가 (선택적)
        if (isStarred) {
            addStarForWish(user, wish);
        }

        // 7. 응답 객체 반환
        return wishMapper.toResponse(wish);
    }


    @Transactional(readOnly = true)
    public PagedResponse<WishListResponse> getWishList(Long userId, Pageable pageable) {
        // 1. 사용자 위시 목록 조회 (페이지 적용)
        List<Wish> wishes = getWishesByUser(userId, pageable);

        // 2. 사용자 전체 위시 수 조회
        long total = getTotalWishesByUser(userId);
        List<WishListResponse> responseList = wishMapper.toWishListResponseList(wishes);

        // 3.  커스텀 페이징 응답 생성
        return buildPagedResponse(responseList, pageable, total);
    }

    @Transactional
    public WishUpdateResponse updateWish(WishUpdateRequest wishUpdateRequest, Long userId, Long wishId, MultipartFile itemImage) {
        // 사용자, 위시 조회
        User user = entityFinder.getUserOrThrow(userId);
        Wish wish = entityFinder.getWishOrThrow(wishId);

        // 다른 사용자의 수정 시도에 대한 예외처리
        validateWishOwnership(wish, userId);
        // 이미지 업로드 처리
        String imageUrl = fileUploadService.uploadFile(itemImage);

        wish.update(
                wishUpdateRequest.getName(),
                wishUpdateRequest.getPrice(),
                wishUpdateRequest.isStarred(),
                imageUrl,
                wishUpdateRequest.getVendor(),
                wishUpdateRequest.getUrl()
        );

        // 별표 처리 로직
        updateStarStatus(user, wish, wishUpdateRequest.isStarred());

        return wishMapper.toWishUpdateResponse(wish);
    }

    @Transactional
    public void deleteWish(Long wishId, Long userId) {
        entityFinder.getUserOrThrow(userId);
        Wish wish = entityFinder.getWishOrThrow(wishId);

        // 다른 사용자의 접근 시도 검증
        validateWishOwnership(wish, userId);

        // 위시 및 Star 삭제
        deleteWishWithStar(wishId);
    }

    @Transactional(readOnly = true)
    public WishDetailResponse getWish(Long wishId, Long userId) {
        entityFinder.getUserOrThrow(userId);
        Wish wish = entityFinder.getWishOrThrow(wishId);

        return wishMapper.toWishDetailResponse(wish);
    }

    @Transactional
    public boolean toggleBoughtStatus(Long wishId, Long userId) {
        entityFinder.getUserOrThrow(userId);
        Wish wish = entityFinder.getWishOrThrow(wishId);

        wish.setBought(!wish.isBought());

        return wish.isBought();
    }

    @Transactional(readOnly = true)
    public WishHighlightResponse highlightWish(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);

        List<Wish> wishList = wishRepository.findByUserIdOrderByNameAsc(userId);

        // 전체 개수만 카운트 쿼리로 조회
        int currentWishCount = wishRepository.countByUser(user);

        // WishDetailResponse 생성
        List<WishDetailResponse> wishDetailResponses = getWishDetailResponses(wishList);

        // wishInfo 객체 생성
        WishHighlightResponse.WishInfo wishInfo = getWishInfo(currentWishCount);

        // WishHighlightResponse 리턴
        return getResponse(wishDetailResponses, wishInfo);
    }

    @Transactional(readOnly = true)
    public PagedResponse<WishListResponse> searchWish(Long userId, WishSearchCondition cond, Pageable pageable) {
        QWish wish = QWish.wish;

        // 기본 조건: userId
        BooleanBuilder builder = new BooleanBuilder()
                .and(wish.user.id.eq(userId));

        // 검색 조건 필터 적용 (키워드, 구매 여부, 즐겨찾기 여부 등)
        applySearchConditions(cond, builder, wish);

        // 정렬 조건 생성
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

        // 전체 개수 조회
        long total = getTotal(wish, builder);

        // 조건에 맞는 Wish 엔티티 목록 조회
        List<Wish> content = getWishes(pageable, wish, builder, orderSpecifiers);

        // DTO 변환 및 페이지 생성
        PageImpl<WishListResponse> page = new PageImpl<>(content.stream()
                .map(WishListResponse::from)
                .toList(), pageable, total);

        // 커스텀 페이징 응답 생성
        return getPagedResponse(page);
    }

    // ------------------------------------------ 아래는 메서드 ------------------------------------------ //
    // 페이지 결과 조회
    private List<Wish> getWishes(Pageable pageable, QWish wish, BooleanBuilder builder, List<OrderSpecifier<?>> orderSpecifiers) {
        return queryFactory
                .selectFrom(wish)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    // 전체 개수 조회 (null일 경우 0L로 대체)
    private Long getTotal(QWish wish, BooleanBuilder builder) {
        return Optional.ofNullable(
                queryFactory
                        .select(wish.count())
                        .from(wish)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);
    }

    // 커스텀 PagedResponse 객체 리턴
    private PagedResponse<WishListResponse> getPagedResponse(PageImpl<WishListResponse> page) {
        return PagedResponse.<WishListResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // 추가 검색 조건 적용 (키워드, 구매 여부, 즐겨찾기 여부)
    private void applySearchConditions(WishSearchCondition cond, BooleanBuilder builder, QWish wish) {
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
    }


    private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
        QWish wish = QWish.wish;
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            boolean isAsc = order.isAscending();

            OrderSpecifier<?> specifier = switch (property) {
                case "name" -> isAsc ? wish.name.asc() : wish.name.desc();
                case "vendor" -> isAsc ? wish.vendor.asc() : wish.vendor.desc();
                case "price" -> isAsc ? wish.price.asc() : wish.price.desc();
                case "createdAt" -> isAsc ? wish.createdAt.asc() : wish.createdAt.desc();
                default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드: " + property);
            };

            orders.add(specifier);
        }

        return orders;
    }

    // 사용자 위시(Wish) 개수가 최대 제한(100개)을 초과하지 않는지 검증합니다.
    private void validateWishLimit(User user) {
        int currentWishCount = wishRepository.countByUser(user);
        if (currentWishCount >= 100) {
            throw new WishException(ErrorCode.WISH_LIMIT_EXCEEDED);
        }
    }

    // 사용자의 별표 위시(Starred Wish) 개수가 최대 5개를 넘지 않도록 검증합니다.
    private void validateStarLimit(Long userId) {
        int currentStarCount = starRepository.countByUserId(userId);
        if (currentStarCount >= 5) {
            throw new StarException(ErrorCode.TOP_WISH_LIMIT_EXCEEDED);
        }
    }

    // 별표 위시로 등록된 경우, 새로운 Star 엔티티를 생성하여 저장합니다.
    private void addStarForWish(User user, Wish wish) {
        int currentStarCount = starRepository.countByUserId(user.getId());  // 최신 rank 계산
        Star star = Star.builder()
                .user(user)
                .wish(wish)
                .rank(currentStarCount + 1)
                .build();
        starRepository.save(star);
    }

    // 사용자 위시 목록 조회 (페이지 적용)
    private List<Wish> getWishesByUser(Long userId, Pageable pageable) {
        QWish wish = QWish.wish;
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

        return queryFactory
                .selectFrom(wish)
                .where(wish.user.id.eq(userId))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    // 사용자 전체 위시 수 조회
    private long getTotalWishesByUser(Long userId) {
        QWish wish = QWish.wish;
        return Optional.ofNullable(
                queryFactory
                        .select(wish.count())
                        .from(wish)
                        .where(wish.user.id.eq(userId))
                        .fetchOne()
        ).orElse(0L);
    }

    // 커스텀 페이징 응답 생성
    private <T> PagedResponse<T> buildPagedResponse(List<T> content, Pageable pageable, long total) {
        PageImpl<T> page = new PageImpl<>(content, pageable, total);

        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
    // 별표 처리 로직 분리
    private void updateStarStatus(User user, Wish wish, boolean isStarred) {
        Long userId = user.getId();
        Long wishId = wish.getId();

        if (!isStarred) {
            starRepository.deleteByWishId(wishId);
        } else {
            boolean alreadyStarred = starRepository.existsByUserIdAndWishId(userId, wishId);
            if (!alreadyStarred) {
                int currentStarCount = starRepository.countByUserId(userId);
                Star star = Star.builder()
                        .user(user)
                        .wish(wish)
                        .rank(currentStarCount + 1)
                        .build();
                starRepository.save(star);
            }
        }
    }

    // 사용자 위시 소유 검증 분리
    private void validateWishOwnership(Wish wish, Long userId) {
        if (!wish.getUser().getId().equals(userId)) {
            throw new WishException(ErrorCode.WISH_FORBIDDEN_ACCESS);
        }
    }

    // 위시 및 별표 삭제
    private void deleteWishWithStar(Long wishId) {
        starRepository.deleteByWishId(wishId);
        wishRepository.deleteById(wishId);
    }

    // WishHighlightResponse.WishDetailResponse 생성
    private List<WishDetailResponse> getWishDetailResponses(List<Wish> wishList) {
        return wishList.stream()
                .limit(3) // 3개만 조회
                .map(wishMapper::toWishDetailResponse)
                .toList();
    }

    // WishHighlightResponse 리턴
    private WishHighlightResponse getResponse(List<WishDetailResponse> wishDetailResponses, WishHighlightResponse.WishInfo wishInfo) {
        return WishHighlightResponse.builder()
                .wishHighlight(wishDetailResponses)
                .wishInfo(wishInfo)
                .build();
    }

    // wishInfo 객체 생성
    private WishHighlightResponse.WishInfo getWishInfo(int currentWishCount) {
        return WishHighlightResponse.WishInfo.builder()
                .currentWishCount(currentWishCount)
                .limitWishCount(MAX_WISH_COUNT)
                .build();
    }
}
