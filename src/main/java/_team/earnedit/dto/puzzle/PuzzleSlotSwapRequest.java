package _team.earnedit.dto.puzzle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleSlotSwapRequest {
    private Long sourceSlotId;
    private Long targetSlotId;
}
