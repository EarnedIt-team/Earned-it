package _team.earnedit.mapper;

import _team.earnedit.dto.puzzle.PuzzleResponse;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.PuzzleSlot;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T00:37:12+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class PuzzleMapperImpl implements PuzzleMapper {

    @Override
    public PuzzleResponse.SlotInfo toSlotInfo(PuzzleSlot slot, Piece piece) {
        if ( slot == null ) {
            return null;
        }

        PuzzleResponse.SlotInfo.SlotInfoBuilder slotInfo = PuzzleResponse.SlotInfo.builder();

        slotInfo.slotIndex( slot.getSlotIndex() );

        slotInfo.isCollected( piece != null && piece.isCollected() );
        slotInfo.pieceId( piece != null ? piece.getId() : null );
        slotInfo.itemId( slot.getItem() != null && piece != null ? slot.getItem().getId() : null );
        slotInfo.itemName( slot.getItem() != null && piece != null ? slot.getItem().getName() : null );
        slotInfo.image( slot.getItem() != null && piece != null ? slot.getItem().getImage() : null );
        slotInfo.value( slot.getItem() != null && piece != null ? slot.getItem().getPrice() : null );
        slotInfo.collectedAt( piece != null ? piece.getCollectedAt() : null );
        slotInfo.isMainPiece( slot.getItem() != null && piece != null && piece.isMain() );

        return slotInfo.build();
    }
}
