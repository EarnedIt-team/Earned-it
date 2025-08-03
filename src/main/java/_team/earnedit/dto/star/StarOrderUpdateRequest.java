package _team.earnedit.dto.star;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "Star 순서 변경 요청 DTO")
public class StarOrderUpdateRequest {
    @Schema(description = "위시 Id 리스트", example = "[34, 32, 31, 35, 30]")
    private List<Long> orderedWishIds;
}
