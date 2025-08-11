package _team.earnedit.dto.puzzle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@Schema(description = "퍼즐 도감 전체 응답")
public class PuzzleResponse {

    @Schema(description = "퍼즐 정보 요약")
    private PuzzleInfo puzzleInfo;

    @Schema(description = "테마별 퍼즐 정보", example = "{\"SWEET_AND_SOUR\": {...}, \"CS_MUST_HAVE\": {...}}")
    private Map<String, PuzzleThemeData> themes;

    @Getter
    @Builder
    @Schema(description = "특정 테마의 퍼즐 정보")
    public static class PuzzleThemeData {

        @Schema(description = "테마 이름", example = "새콤? 달콤!")
        private String themeName;

        @Schema(description = "수집한 조각 개수", example = "2")
        private int collectedCount;

        @Schema(description = "전체 조각 개수", example = "6")
        private int totalCount;

        @Schema(description = "수집한 조각의 총 금액", example = "458000")
        private long totalValue;

        @Schema(description = "퍼즐 슬롯 리스트")
        private List<SlotInfo> slots;
    }

    @Getter
    @Builder
    @Schema(description = "퍼즐 슬롯 단위 정보")
    public static class SlotInfo {

        @Schema(description = "퍼즐 슬롯 인덱스", example = "0")
        private int slotIndex;

        @Schema(description = "수집 여부", example = "true")
        private boolean isCollected;

        @Schema(description = "조각 ID (수집된 경우에만 존재)", example = "101")
        private Long pieceId;

        @Schema(description = "아이템 ID (수집된 경우에만 존재)", example = "10")
        private Long itemId;

        @Schema(description = "아이템 이름 (수집된 경우에만 존재)", example = "에어팟 프로 2세대")
        private String itemName;

        @Schema(description = "아이템 이미지 URL", example = "https://cdn.example.com/images/item.jpg")
        private String image;

        @Schema(description = "아이템 가격", example = "329000")
        private Long value;

        @Schema(description = "수집한 시각", example = "2025-07-31T12:00:00")
        private LocalDateTime collectedAt;
    }

    @Getter
    @Builder
    @Schema(description = "퍼즐 정보 요약")
    public static class PuzzleInfo {

        @Schema(description = "전체 테마 개수", example = "2")
        private long themeCount;

        @Schema(description = "현재 완성한 테마 개수", example = "1")
        private long completedThemeCount;

        @Schema(description = "전체 조각 개수", example = "12")
        private long totalPieceCount;

        @Schema(description = "현재 완성한 조각 개수", example = "3")
        private long completedPieceCount;

        @Schema(description = "전체 누적 금액(원)", example = "33330")
        private long totalAccumulatedValue;

    }
}