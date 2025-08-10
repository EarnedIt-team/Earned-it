package _team.earnedit.dto.main;

import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.wish.WishListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "메인 페이지 응답 객체")
public class MainPageResponse {

    @Schema(description = "유저 정보")
    private UserInfo userInfo;

    @Schema(description = "Top 5 위시 리스트")
    private List<WishListResponse> starWishes;

    @Schema(description = "조각 정보")
    private PieceResponse pieceInfo;

    @Getter
    @Builder
    @Schema(description = "유저 정보 객체")
    public static class UserInfo {

        @Schema(description = "현재 자산 금액", example = "5321000")
        private long amount;

        @Schema(description = "초당 자산 증가량", example = "12.53")
        private double amountPerSec;

        @Schema(description = "급여 여부", example = "true")
        private boolean hasSalary;

        @Schema(description = "급여일 (일자)", example = "25")
        private int payday;

        @Schema(description = "출석체크 여부")
        private boolean isCheckedIn;
    }
}
