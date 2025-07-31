package _team.earnedit.dto.puzzle;


import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "퍼즐 응답 DTO")
public class PuzzleResponse {
    private PuzzleInfo puzzleInfo;
    private Themes themes;

    @Builder
    @Getter
    @Schema(description = "퍼즐 통계")
    public static class PuzzleInfo {
        private
    }

    @Builder
    @Getter
    @Schema(description = "테마의 아이템")
    public static class Themes {
        List<Piece> pieces;

    }

}
