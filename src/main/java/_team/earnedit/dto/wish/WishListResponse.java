package _team.earnedit.dto.wish;

import _team.earnedit.entity.Wish;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@Schema(description = "위시 아이템 응답 DTO")
public class WishListResponse {
    @Schema(description = "위시 ID", example = "1")
    private Long wishId;

    @Schema(description = "사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "아이템 이름", example = "새콤달콤")
    private String name;

    @Schema(description = "가격", example = "85000")
    private long price;

    @Schema(description = "아이템 이미지 URL", example = "https://cdn.example.com/images/item123.png")
    private String itemImage;

    @Schema(description = "구매 여부", example = "false")
    private boolean isBought;

    @Schema(description = "판매자/브랜드명", example = "ABC")
    private String vendor;

    @Schema(description = "생성일시", example = "2025-07-31T14:22:00")
    private LocalDateTime createdAt;

    @Schema(description = "Top 5 즐겨찾기 여부", example = "true")
    private boolean isStarred;

    @Schema(description = "구매 링크", example = "https://naver.com/shoplink.png")
    private String url;

    public static WishListResponse from(Wish wish) {
        return WishListResponse.builder()
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
                .build();
    }
}
