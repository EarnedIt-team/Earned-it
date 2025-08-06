package _team.earnedit.service;

import _team.earnedit.dto.PagedResponse;
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
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final FileUploadService fileUploadService;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public WishAddResponse addWish(WishAddRequest wishAddRequest, Long userId, MultipartFile itemImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 위시 개수 제한 확인
        int currentWishCount = wishRepository.countByUser(user);
        if (currentWishCount >= 100) {
            throw new WishException(ErrorCode.WISH_LIMIT_EXCEEDED); // 새 에러코드 정의 필요
        }

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


    @Transactional(readOnly = true)
    public PagedResponse<WishListResponse> getWishList(Long userId, Pageable pageable) {
        QWish wish = QWish.wish;

        // 정렬 조건을 Pageable로부터 QueryDSL용 OrderSpecifier로 변환
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

        List<Wish> content = queryFactory
                .selectFrom(wish)
                .where(wish.user.id.eq(userId))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(wish.count())
                        .from(wish)
                        .where(wish.user.id.eq(userId))
                        .fetchOne()
        ).orElse(0L);

        List<WishListResponse> responseList = content.stream()
                .map(WishListResponse::from)
                .toList();

        PageImpl<WishListResponse> page = new PageImpl<>(responseList, pageable, total);

        return PagedResponse.<WishListResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Transactional
    public WishUpdateResponse updateWish(WishUpdateRequest wishUpdateRequest, Long userId, Long wishId, MultipartFile itemImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Wish wish = wishRepository.findById(wishId).orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));

        // 이미지 업로드 처리
        String imageUrl = fileUploadService.uploadFile(itemImage);

        // 다른 사용자의 수정 시도에 대한 예외처리
        if (!wish.getUser().getId().equals(userId)) {
            throw new WishException(ErrorCode.WISH_UPDATE_FORBIDDEN);
        }

        wish.update(
                wishUpdateRequest.getName(),
                wishUpdateRequest.getPrice(),
                wishUpdateRequest.isStarred(),
                imageUrl,
                wishUpdateRequest.getVendor(),
                wishUpdateRequest.getUrl()
        );

        if (!wishUpdateRequest.isStarred()) {
            // star 제거
            starRepository.deleteByWishId(wishId);
        } else {
            // 중복 방지: 이미 Star가 있는지 확인
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

        return WishUpdateResponse.builder()
                .wishId(wish.getId())
                .name(wish.getName())
                .ItemImage(imageUrl)
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
    public WishHighlightResponse highlightWish(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<Wish> wishList = wishRepository.findByUserIdOrderByNameAsc(userId);

        // 전체 개수만 카운트 쿼리로 조회
        int currentWishCount = wishRepository.countByUser(user);

        // WishHighlightResponse.WishDetailResponse 생성
        List<WishDetailResponse> wishDetailResponses = wishList.stream()
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

        // WishHighlightResponse.wishInfo 객체 생성
        WishHighlightResponse.WishInfo wishInfo = WishHighlightResponse.WishInfo.builder()
                .currentWishCount(currentWishCount)
                .limitWishCount(MAX_WISH_COUNT)
                .build();


        // WishHighlightResponse 리턴
        return WishHighlightResponse.builder()
                .wishHighlight(wishDetailResponses)
                .wishInfo(wishInfo)
                .build();
    }

    @Transactional(readOnly = true)
    public PagedResponse<WishListResponse> searchWish(Long userId, WishSearchCondition cond, Pageable pageable) {
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

        // 정렬 조건을 Pageable로부터 QueryDSL용 OrderSpecifier로 변환
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

        // 전체 개수 조회 (null일 경우 0L로 대체)
        long total = Optional.ofNullable(
                queryFactory
                        .select(wish.count())
                        .from(wish)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        // 페이지 결과 조회
        List<Wish> content = queryFactory
                .selectFrom(wish)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        PageImpl<WishListResponse> page = new PageImpl<>(content.stream().map(WishListResponse::from).toList(), pageable, total);

        return PagedResponse.<WishListResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
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
}
