package _team.earnedit.dto.wish;

import _team.earnedit.entity.Wish;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class WishListResponse {
    private Long id;
    private Long userId;
    private String name;
    private int price;
    private String itemImage;
    private boolean isBought;
    private String vendor;
    private LocalDateTime createdAt;
    private boolean isStarred;


    public static WishListResponse from(Wish wish) {
        return WishListResponse.builder()
                .id(wish.getId())
                .userId(wish.getUser().getId())
                .name(wish.getName())
                .price(wish.getPrice())
                .itemImage(wish.getItemImage())
                .isBought(wish.isBought())
                .vendor(wish.getVendor())
                .createdAt(wish.getCreatedAt())
                .isStarred(wish.isStarred())
                .build();
    }
}
