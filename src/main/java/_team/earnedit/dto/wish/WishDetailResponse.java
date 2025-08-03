package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@Schema(description = "위시 상세 응답 DTO")
public class WishDetailResponse {

    @Schema(description = "위시 ID", example = "1")
    private Long wishId;

    @Schema(description = "사용자 ID", example = "1001")
    private Long userId;

    @Schema(description = "위시 이름", example = "무선 키보드")
    private String name;

    @Schema(description = "가격", example = "49000")
    private int price;

    @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/images/keyboard.png")
    private String itemImage;

    @Schema(description = "구매 여부", example = "false")
    private boolean isBought;

    @Schema(description = "판매처", example = "쿠팡")
    private String vendor;

    @Schema(description = "생성 시각", example = "2025-07-27T13:45:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-07-28T08:15:00")
    private LocalDateTime updatedAt;

    @Schema(description = "상품 상세 URL", example = "https://www.example.com/product/keyboard")
    private String url;

    @Schema(description = "Top 5 위시 여부", example = "true")
    private boolean isStarred;
}
