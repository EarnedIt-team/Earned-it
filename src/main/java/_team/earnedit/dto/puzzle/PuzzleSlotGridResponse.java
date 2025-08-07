package _team.earnedit.dto.puzzle;

import _team.earnedit.entity.PuzzleSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PuzzleSlotGridResponse {
    private int slotIndex;
    private String itemName;

    public static PuzzleSlotGridResponse from(PuzzleSlot slot) {
        return new PuzzleSlotGridResponse(
                slot.getSlotIndex(),
                slot.getItem() != null ? slot.getItem().getName() : null
        );
    }
}