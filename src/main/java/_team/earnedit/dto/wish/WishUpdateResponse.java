package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "위시 수정 응답 DTO")
public class WishUpdateResponse {

    @Schema(description = "위시 ID", example = "1")
    private long wishId;

    @Schema(description = "상품 이름", example = "무선 마우스")
    private String name;

    @Schema(description = "판매처", example = "네이버 스마트스토어")
    private String vendor;

    @Schema(description = "가격", example = "29900")
    private long price;

    @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/images/mouse.png")
    private String ItemImage;

    @Schema(description = "상품 구매 URL", example = "https://smartstore.naver.com/item/123")
    private String url;

    @Schema(description = "수정된 시각", example = "2025-07-28T14:23:00")
    private LocalDateTime updatedAt;
}
