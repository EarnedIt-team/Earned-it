package _team.earnedit.mapper;

import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.entity.Piece;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PieceMapper {

    @Mapping(target = "pieceId", source = "id")
    @Mapping(target = "price", source = "item.price")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "image", source = "item.image")
    @Mapping(target = "rarity", source = "item.rarity")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "vendor", source = "item.vendor")
    @Mapping(target = "isMain", source = "main")
    PieceResponse toPieceResponse(Piece piece);
}
