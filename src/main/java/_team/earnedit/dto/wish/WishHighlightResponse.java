package _team.earnedit.dto.wish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "위시 하이라이트 응답 DTO")
public class WishHighlightResponse {

    private WishInfo wishInfo;
    private List<WishDetailResponse> wishDetailResponse;


    @Builder
    @Getter
    @Schema(description = "위시 개수 응답 DTO")
    public static class WishInfo {
        private int currentWishCount;
    }
}
