package _team.earnedit.dto.wish;

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
}
