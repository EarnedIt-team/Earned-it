package _team.earnedit.dto.term;

import _team.earnedit.entity.Term;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermRequestDto {

    private Term.Type type;

    private boolean isChecked;

}
