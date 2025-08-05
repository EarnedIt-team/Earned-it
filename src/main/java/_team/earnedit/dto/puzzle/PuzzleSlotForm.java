package _team.earnedit.dto.puzzle;

import _team.earnedit.entity.Theme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuzzleSlotForm {
    private Long id;
    private Long itemId;
    private Theme theme;
    private int slotIndex;
}
