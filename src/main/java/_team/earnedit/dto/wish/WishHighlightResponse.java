package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "위시 하이라이트 응답 DTO")
public class WishHighlightResponse {

    @Schema(description = "위시 개수 정보")
    private WishInfo wishInfo;

    @Schema(description = "최대 3개의 위시 목록")
    private List<WishDetailResponse> wishHighlight;

    @Builder
    @Getter
    @Schema(description = "위시 개수 응답 DTO")
    public static class WishInfo {

        @Schema(description = "현재 등록된 위시 개수", example = "23")
        private int currentWishCount;

        @Schema(description = "등록 가능한 최대 위시 개수", example = "100")
        private int limitWishCount;
    }
}
