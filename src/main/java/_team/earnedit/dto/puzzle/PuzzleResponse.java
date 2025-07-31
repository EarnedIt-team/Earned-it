package _team.earnedit.dto.puzzle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PuzzleResponse {
    private Map<String, PuzzleThemeData> data;

    @Getter
    @Builder
    public static class PuzzleThemeData {
        private String themeName;
        private int collectedCount;
        private int totalCount;
        private long totalValue;
        private List<SlotInfo> slots;
    }

    @Getter
    @Builder
    public static class SlotInfo {
        private int slotIndex;
        private boolean isCollected;
        private Long itemId;
        private String itemName;
        private String image;
        private Long value;
        private String collectedAt;
    }
}
