package _team.earnedit.mapper;

import _team.earnedit.dto.main.MainPageResponse;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.Rarity;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T00:37:12+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class MainPageMapperImpl implements MainPageMapper {

    @Override
    public PieceResponse toPieceResponse(Piece piece) {
        if ( piece == null ) {
            return null;
        }

        PieceResponse.PieceResponseBuilder pieceResponse = PieceResponse.builder();

        pieceResponse.pieceId( piece.getId() );
        pieceResponse.collectedAt( piece.getCollectedAt() );
        pieceResponse.price( pieceItemPrice( piece ) );
        pieceResponse.rarity( pieceItemRarity( piece ) );
        pieceResponse.name( pieceItemName( piece ) );
        pieceResponse.image( pieceItemImage( piece ) );
        pieceResponse.vendor( pieceItemVendor( piece ) );
        pieceResponse.description( pieceItemDescription( piece ) );
        pieceResponse.isMainPiece( piece.isMain() );

        return pieceResponse.build();
    }

    @Override
    public MainPageResponse.UserInfo toUserInfo(User user, Salary salary) {
        if ( user == null && salary == null ) {
            return null;
        }

        MainPageResponse.UserInfo.UserInfoBuilder userInfo = MainPageResponse.UserInfo.builder();

        if ( user != null ) {
            if ( user.getIsCheckedIn() != null ) {
                userInfo.isCheckedIn( user.getIsCheckedIn() );
            }
        }
        if ( salary != null ) {
            if ( salary.getAmount() != null ) {
                userInfo.amount( salary.getAmount() );
            }
            else {
                userInfo.amount( 0L );
            }
            if ( salary.getAmountPerSec() != null ) {
                userInfo.amountPerSec( salary.getAmountPerSec() );
            }
            else {
                userInfo.amountPerSec( 0.0 );
            }
            if ( salary.getPayday() != null ) {
                userInfo.payday( salary.getPayday() );
            }
            else {
                userInfo.payday( 0 );
            }
        }
        userInfo.hasSalary( salary != null );

        return userInfo.build();
    }

    @Override
    public WishListResponse toWishListResponse(Wish wish) {
        if ( wish == null ) {
            return null;
        }

        WishListResponse.WishListResponseBuilder wishListResponse = WishListResponse.builder();

        wishListResponse.wishId( wish.getId() );
        wishListResponse.userId( wishUserId( wish ) );
        wishListResponse.isBought( wish.isBought() );
        wishListResponse.isStarred( wish.isStarred() );
        wishListResponse.name( wish.getName() );
        wishListResponse.price( wish.getPrice() );
        wishListResponse.itemImage( wish.getItemImage() );
        wishListResponse.vendor( wish.getVendor() );
        wishListResponse.createdAt( wish.getCreatedAt() );
        wishListResponse.url( wish.getUrl() );

        return wishListResponse.build();
    }

    private long pieceItemPrice(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return 0L;
        }
        return item.getPrice();
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

    private String pieceItemImage(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getImage();
    }

    private String pieceItemVendor(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getVendor();
    }

    private String pieceItemDescription(Piece piece) {
        Item item = piece.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getDescription();
    }

    private Long wishUserId(Wish wish) {
        User user = wish.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
