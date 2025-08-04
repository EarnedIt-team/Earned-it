package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishSearchCondition {
    @Schema(description = "검색 키워드 (이름 또는 회사명)", example = "청바지")
    private String keyword;

    @Schema(description = "구매 완료 여부", example = "true")
    private Boolean isBought;

    @Schema(description = "별표 여부", example = "false")
    private Boolean isStarred;

    @Schema(description = "정렬 기준 필드", example = "price")
    private String sort;

    @Schema(description = "정렬 방향", example = "asc")
    private String direction;
}
