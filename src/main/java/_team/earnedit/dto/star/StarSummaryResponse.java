package _team.earnedit.dto.star;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StarSummaryResponse {
    @Schema(description = "Star ID", example = "1")
    private Long starId;

    @Schema(description = "사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "Wish ID", example = "1")
    private Long wishId;

    @Schema(description = "아이템 이름", example = "새콤달콤")
    private String name;

    @Schema(description = "판매자/브랜드명", example = "ABC")
    private String vendor;

    @Schema(description = "가격", example = "85000")
    private long price;

    @Schema(description = "아이템 이미지 URL", example = "https://cdn.example.com/images/item123.png")
    private String itemImage;

    @Schema(description = "구매 여부", example = "false")
    private boolean isBought;

    @Schema(description = "Star 여부", example = "false")
    private boolean starred;

    @Schema(description = "순서", example = "1~5")
    private int rank;
}
