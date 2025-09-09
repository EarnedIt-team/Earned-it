package _team.earnedit.mapper;

import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.Rarity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T00:37:12+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class PieceMapperImpl implements PieceMapper {

    @Override
    public PieceResponse toPieceResponse(Piece piece) {
        if ( piece == null ) {
            return null;
        }

        PieceResponse.PieceResponseBuilder pieceResponse = PieceResponse.builder();

        pieceResponse.pieceId( piece.getId() );
        pieceResponse.price( pieceItemPrice( piece ) );
        pieceResponse.description( pieceItemDescription( piece ) );
        pieceResponse.image( pieceItemImage( piece ) );
        pieceResponse.rarity( pieceItemRarity( piece ) );
        pieceResponse.name( pieceItemName( piece ) );
        pieceResponse.vendor( pieceItemVendor( piece ) );
        pieceResponse.isMainPiece( piece.isMain() );
        pieceResponse.collectedAt( piece.getCollectedAt() );

        return pieceResponse.build();
    }

    private long pieceItemPrice(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return 0L;
        }
        return item.getPrice();
    }

    private String pieceItemDescription(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getDescription();
    }

    private String pieceItemImage(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getImage();
    }

    private Rarity pieceItemRarity(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getRarity();
    }

    private String pieceItemName(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getName();
    }

    private String pieceItemVendor(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getVendor();
    }
}
