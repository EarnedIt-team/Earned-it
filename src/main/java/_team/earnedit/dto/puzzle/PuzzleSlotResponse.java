package _team.earnedit.dto.puzzle;

import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleSlotResponse {

    private Long slotId;
    private int slotIndex;
    private Theme theme;

    private Long itemId;
    private String itemName;
    private String itemImage;

    public static PuzzleSlotResponse from(PuzzleSlot slot) {
        return PuzzleSlotResponse.builder()
                .slotId(slot.getId())
                .slotIndex(slot.getSlotIndex())
                .theme(slot.getTheme())
                .itemId(slot.getItem().getId())
                .itemName(slot.getItem().getName())
                .itemImage(slot.getItem().getImage())
                .build();
    }
}