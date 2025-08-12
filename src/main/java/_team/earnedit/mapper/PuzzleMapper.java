package _team.earnedit.mapper;

import _team.earnedit.dto.puzzle.PuzzleResponse;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.PuzzleSlot;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PuzzleMapper {

    // PuzzleSlot + Piece → SlotInfo
    @Mapping(target = "slotIndex", source = "slot.slotIndex")
    @Mapping(target = "isCollected", expression = "java(piece != null && piece.isCollected())")
    @Mapping(target = "pieceId", expression = "java(piece != null ? piece.getId() : null)")
    @Mapping(target = "itemId", expression = "java(slot.getItem() != null && piece != null ? slot.getItem().getId() : null)")
    @Mapping(target = "itemName", expression = "java(slot.getItem() != null && piece != null ? slot.getItem().getName() : null)")
    @Mapping(target = "image", expression = "java(slot.getItem() != null && piece != null ? slot.getItem().getImage() : null)")
    @Mapping(target = "value", expression = "java(slot.getItem() != null && piece != null ? slot.getItem().getPrice() : null)")
    @Mapping(target = "collectedAt", expression = "java(piece != null ? piece.getCollectedAt() : null)")
    @Mapping(target = "isMainPiece", expression = "java(slot.getItem() != null && piece != null && piece.isMain())")
    PuzzleResponse.SlotInfo toSlotInfo(PuzzleSlot slot, @Context Piece piece);
}
